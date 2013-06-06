package bioner.normalization;

import bioner.normalization.data.BioNERCandidate;

public class RankCandidate {
	public static void RankCandidate(BioNERCandidate[] candidates)
	{
		quickSort(candidates);
	}
	
	private static void quickSort(BioNERCandidate[] array) {
	        quickSort(array, 0, array.length - 1);
	}

    private static void quickSort(BioNERCandidate[] array, int low, int high) {
        if (low < high) {
            int p = partition(array, low, high);
            quickSort(array, low, p - 1);
            quickSort(array, p + 1, high);
        }

    }

    private static int partition(BioNERCandidate[] array, int low, int high) {
        double s = array[high].getScore();
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (array[j].getScore() > s) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, ++i, high);
        return i;
    }

    private static void swap(BioNERCandidate[] array, int i, int j) {
    	BioNERCandidate temp;
        temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

}
