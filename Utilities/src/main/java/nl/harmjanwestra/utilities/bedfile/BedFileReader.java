/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.harmjanwestra.utilities.bedfile;

import nl.harmjanwestra.utilities.enums.Chromosome;
import nl.harmjanwestra.utilities.enums.Strand;
import nl.harmjanwestra.utilities.features.*;
import umcg.genetica.io.text.TextFile;
import umcg.genetica.text.Strings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * @author Harm-Jan
 */
public class BedFileReader {

	public BedFileReader() {

	}

	Pattern splitpattern = Strings.whitespace;

	public BedFileReader(Pattern splitpattern) {
		this.splitpattern = splitpattern;
	}


	protected int nrFeatures;
	protected int featureLengthSum;
	protected BedFileFeatureFilter filter = null;

	public void setFilter(BedFileFeatureFilter filter) {
		this.filter = filter;
	}

//	public Track readAsTrack(String file, String name, boolean removeduplicates) throws IOException {
//		nrFeatures = 0;
//		featureLengthSum = 0;
//		TextFile tf = new TextFile(file, TextFile.R);
//
//		System.out.println("Reading file: " + file);
//
//		// chr1	8128340	8128539	C011PABXX110504:4:2203:14692:158380	0	-
//		String[] elems = tf.readLineElems(splitpattern);
//		Track track = new Track(name, removeduplicates);
//
//		while (elems != null) {
//			Feature f = parseElems(elems);
//			if (f != null) {
//				track.addFeature(f);
//			}
//			elems = tf.readLineElems(splitpattern);
//		}
//
//		tf.close();
//
//		System.out.println("Average feature featureLengthSum: " + ((double) featureLengthSum / nrFeatures) + "\tNumber of elements: " + nrFeatures);
//		track.printNrFeatures();
//		return track;
//	}

	public ArrayList<Feature> readAsList(String file) throws IOException {

		TextFile tf = new TextFile(file, TextFile.R);

		// chr1	8128340	8128539	C011PABXX110504:4:2203:14692:158380	0	-
		String ln = tf.readLine();

		ArrayList<Feature> allFeatures = new ArrayList<Feature>();
		while (ln != null) {
			if (ln.startsWith("#") || ln.startsWith("track")) {

			} else {
				String[] elems = splitpattern.split(ln);
				Feature f = parseElems(elems);
				if (f != null) {
					allFeatures.add(f);
				}
			}
			ln = tf.readLine();
		}

		tf.close();
		Collections.sort(allFeatures, new FeatureComparator(false));
		return allFeatures;
	}

	protected Feature parseElems(String[] elems) {

		if (elems.length > 2) {
			int len = 0;
			Chromosome featureChr = Chromosome.parseChr(elems[0]);

			Strand featureStrand = Strand.NA;

			featureStrand = Strand.parseStr(elems[elems.length - 1]);

			int featureStart = -1;
			int featureStop = -1;
			try {
				featureStart = Integer.parseInt(elems[1]);
			} catch (NumberFormatException e) {
				System.out.println("Could not parse chromosome start position: " + elems[1]);
			}

			try {
				featureStop = Integer.parseInt(elems[2]);
			} catch (NumberFormatException e) {
				System.out.println("Could not parse chromosome stop position: " + elems[2]);
			}


			len = featureStop - featureStart;
			featureLengthSum += len;
			nrFeatures++;
			Feature f = new Feature();
			f.setChromosome(featureChr);
			f.setStrand(featureStrand);
			f.setStart(featureStart);
			f.setStop(featureStop);


			if (filter == null || filter.passesFilter(f)) {
				return f;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

}
