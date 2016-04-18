package nl.harmjanwestra.broshifter;

import nl.harmjanwestra.broshifter.CLI.GoShifterOptions;
import umcg.genetica.containers.Pair;
import umcg.genetica.io.Gpio;
import umcg.genetica.io.text.TextFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * Created by hwestra on 11/11/15.
 */
public class GoShifter {

	boolean DEBUG = false;

	public static void main(String[] args) {

		String[] arguments = new String[]{
				"-a", "/Data/tmp/2016-03-25/annot.txt",
				"-i", "/Data/tmp/2016-03-25/oldProxies-broshifter.txt",
				"-o", "/Data/tmp/2016-03-25/RA-goshifter2"
		};

		try {
			GoShifter shiftah = new GoShifter(new GoShifterOptions(arguments));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public GoShifter(GoShifterOptions options) throws IOException {
		this.options = options;
		run();
	}

	GoShifterOptions options;

	public void run() throws IOException {
		String listOfAnnotations = options.listOfAnnotations;
		String outfile = options.outfile;
		boolean conditional = options.conditional;
		int nrThreads = options.nrThreads;

		// TODO: this input file needs some standard formatting or something
		TextFile tf = new TextFile(listOfAnnotations, TextFile.R);
		ArrayList<String> annotationFiles = tf.readAsArrayList();
		System.out.println(annotationFiles.size() + " annotation files in: " + listOfAnnotations);
		tf.close();

		annotationFiles = checkFiles(annotationFiles);
		System.out.println(annotationFiles.size() + " annotation files found on disk.");

		// start threadpool
		ExecutorService threadPool = Executors.newFixedThreadPool(nrThreads);
		CompletionService<Pair<String, ArrayList<String>>> jobHandler = new ExecutorCompletionService<Pair<String, ArrayList<String>>>(threadPool);

		System.out.println("Spinning up a threadpool of " + nrThreads);

		String headerOverall = "P"
				+ "\tNrOfLociWithOverlap"
				+ "\tEnrichment"
				+ "\tMeanNrOfLociWithOverlapNull"
				+ "\ttotalNrOfOverlappingVariants"
				+ "\tTotalNrOfVariants"
				+ "\tPercentageOfOverlappingVariants"
				+ "\tAnnotation1";
		if (conditional) {
			headerOverall += "\tAnnotation2";
		}

		String headerLocus = "LocusScore"
				+ "\tQuerySNP "
				+ "\tRegionName"
				+ "\tOriginalRegion"
				+ "\tNrOverlapping"
				+ "\tNrTotalSNPs"
				+ "\tPercentageOverlappingSNPs"
				+ "\tAnnotation1"
				+ "\tNumberOfAnnotation1Regions";
		if (conditional) {
			headerLocus += "\tAnnotation2";
			headerLocus += "\tNrAnnotation2Regions";
		}


		TextFile outOverall = new TextFile(outfile + "-Overall.txt", TextFile.W);
		TextFile outLocus = new TextFile(outfile + "-Locus.txt", TextFile.W);
		outOverall.writeln(headerOverall);
		outLocus.writeln(headerLocus);


		// for each annotatios
		submitted = 0;
		returned = 0;
		if (conditional) {
			// conditional task
			for (int a1 = 0; a1 < annotationFiles.size(); a1++) {
				String annotation1 = annotationFiles.get(a1);
				for (int a2 = 0; a2 < annotationFiles.size(); a2++) {
					if (a2 != a1 || DEBUG) {

						String annotation2 = annotationFiles.get(a2);
						GoShifterTask task = new GoShifterTask(
								submitted,
								annotation1,
								annotation2,
								options);
						submitted++;
						task.DEBUG = DEBUG;
						jobHandler.submit(task);
					}
				}
			}
		} else {
			for (String annotation1 : annotationFiles) {
				// normal enrichment task
				GoShifterTask task = new GoShifterTask(submitted,
						annotation1,
						null,
						options);
				submitted++;
				task.DEBUG = DEBUG;
				jobHandler.submit(task);
			}
		}

		System.out.println(submitted + " jobs submitted.");

		clearQueue(outOverall, outLocus, jobHandler);


		outOverall.close();
		outLocus.close();

		System.out.println("Main: done testing. Overall results are here: " + outOverall.getFileName());
		System.out.println("Main: done testing. Locus results are here: " + outLocus.getFileName());
		System.out.println();

		threadPool.shutdown();
	}

	int returned = 0;
	int submitted = 0;

	private void clearQueue(TextFile outOverall, TextFile outLocus, CompletionService<Pair<String, ArrayList<String>>> jobHandler) throws IOException {
		System.out.println(submitted + " results to process.");
		while (returned < submitted) {
			try {
				Pair<String, ArrayList<String>> output = jobHandler.take().get();
				if (output != null) {
					String overallStr = output.getLeft();
					outOverall.writeln(overallStr);
					ArrayList<String> locusSpecific = output.getRight();
					for (String s : locusSpecific) {
						outLocus.writeln(s);
					}
					outOverall.flush();
					outLocus.flush();
					if (returned % 10 == 0 && returned > 0) {
						System.out.println("\nMain: " + returned + " out of " + submitted + " jobs completed\n");
					}
				}
				returned++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		returned = 0;
		submitted = 0;
	}


	private ArrayList<String> checkFiles(ArrayList<String> annotationFiles) {
		ArrayList<String> filesPresent = new ArrayList<String>();
		for (int i = 0; i < annotationFiles.size(); i++) {
			if (Gpio.exists(annotationFiles.get(i))) {
				filesPresent.add(annotationFiles.get(i));
			} else {
				System.err.println("WARNING: could not find file: " + annotationFiles.get(i));
			}
		}
		return filesPresent;
	}


}
