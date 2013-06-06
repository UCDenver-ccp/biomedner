package bioner.normalization;

public class RankDouble {
	public static void Rank(double[] genes)
	{
		quickSort(genes);
	}
	
	private static void quickSort(double[] array) {
	        quickSort(array, 0, array.length - 1);
	}

    private static void quickSort(double[] array, int low, int high) {
        if (low < high) {
            int p = partition(array, low, high);
            quickSort(array, low, p - 1);
            quickSort(array, p + 1, high);
        }

    }

    private static int partition(double[] array, int low, int high) {
        double s = array[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (array[j] > s) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, ++i, high);
        return i;
    }

    private static void swap(double[] array, int i, int j) {
    	double temp;
        temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
