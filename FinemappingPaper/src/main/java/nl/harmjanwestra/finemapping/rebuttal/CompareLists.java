package nl.harmjanwestra.finemapping.rebuttal;

public class CompareLists {
	
	public static void main(String[] args) {
		
		String[] ra = new String[]{
				"Chr1_2406887-2785671",
				"Chr1_17597181-17679598",
				"Chr1_113863087-114527968",
				"Chr2_60914729-61892409",
				"Chr2_100544954-101038647",
				"Chr2_191873553-192007734",
				"Chr2_204446380-204816382",
				"Chr3_58154177-58549297",
				"Chr4_26028805-26134465",
				"Chr5_55414956-55447909",
				"Chr5_102098582-102711659",
				"Chr6_90806835-91039808",
				"Chr6_137882875-138275085",
				"Chr7_37363978-37440453",
				"Chr7_128549568-128777520",
				"Chr9_34649442-34974974",
				"Chr15_38814377-38972177",
				"Chr17_37382674-38240216",
				"Chr18_12738413-12924117",
				"Chr19_10396336-10628468"
		};
		
		String[] t1d = new String[]{
				"Chr1_113863087-114527968",
				"Chr1_206802440-207032751",
				"Chr2_100544954-101038647",
				"Chr2_162960873-163361685",
				"Chr2_204446380-204816382",
				"Chr4_122973062-123565302",
				"Chr5_35799296-36034866",
				"Chr6_90806835-91039808",
				"Chr6_126442383-127412057",
				"Chr7_50366637-50694384",
				"Chr10_6030243-6169685",
				"Chr10_90008047-90220370",
				"Chr11_2178940-2282254",
				"Chr12_9638661-9972763",
				"Chr12_56351937-56833224",
				"Chr12_111699146-113030487",
				"Chr13_100036418-100108807",
				"Chr14_98368955-98530673",
				"Chr14_101290463-101328739",
				"Chr15_38814377-38972177",
				"Chr15_79020743-79263361",
				"Chr16_11017058-11307024",
				"Chr16_11313051-11477919",
				"Chr16_28295306-29027807",
				"Chr16_75202998-75521030",
				"Chr17_38718890-38878827",
				"Chr18_12738413-12924117",
				"Chr18_67480632-67569547",
				"Chr19_10396336-10628468",
				"Chr19_49094856-49278082",
				"Chr20_1497197-1689461",
				"Chr21_43810084-43887145",
				"Chr22_29925296-30668308",
				"Chr22_37568670-37661414\n"
		};
		
		String[] comb = new String[]{
				"Chr1_17597181-17679598",
				"Chr1_113863087-114527968",
				"Chr2_100544954-101038647",
				"Chr2_162960873-163361685",
				"Chr2_191873553-192007734",
				"Chr2_204446380-204816382",
				"Chr3_45929800-46650993",
				"Chr4_26028805-26134465",
				"Chr5_55414956-55447909",
				"Chr5_102098582-102711659",
				"Chr6_90806835-91039808",
				"Chr6_137882875-138275085",
				"Chr7_37363978-37440453",
				"Chr10_6030243-6169685",
				"Chr10_6388071-6545104",
				"Chr11_2178940-2282254",
				"Chr12_57626582-58488667",
				"Chr12_111699146-113030487",
				"Chr15_38814377-38972177",
				"Chr16_11017058-11307024",
				"Chr16_11313051-11477919",
				"Chr16_75202998-75521030",
				"Chr17_37382674-38240216",
				"Chr18_12738413-12924117",
				"Chr18_67480632-67569547",
				"Chr19_10396336-10628468",
				"Chr21_43810084-43887145",
				"Chr22_29925296-30668308\n"
		};
		
		int overlap = 0;
		int overlap2 = 0;
		int overlap3 = 0;
		for (String s : ra) {
			for (String s2 : t1d) {
				if (s.equals(s2)) {
					overlap++;
				}
			}
			for (String s2 : comb) {
				if (s.equals(s2)) {
					overlap2++;
				}
			}
		}
		for (String s : t1d) {
			for (String s2 : comb) {
				if (s.equals(s2)) {
					overlap3++;
				}
			}
		}
		System.out.println(overlap + "\t" + overlap2);
		System.out.println(overlap3);
	}
	
	}
