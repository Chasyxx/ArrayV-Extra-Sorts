package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.BinaryInsertionSorting;

// SELECTIQUICK SORT
// A weird hybrid of quick sort where selection sort wants to make sure the partitions are of equal size
// and there's binary insertion sort at the end for some reason

// Copyright (C) 2024 Chase Taylor
// Under the Expat license AKA MIT license

// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:

// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.


/**
 * @author Chasyxx
 *
 */
public class SelectiquickSort extends BinaryInsertionSorting {
    public SelectiquickSort(ArrayVisualizer arrayV) {
        super(arrayV);
        this.setSortListName("Selectiquick");
        this.setRunAllSortsName("Chasyxx's Selectiquick sort");
        this.setRunSortName("Selectiquick Sort");
        this.setCategory("Hybrid Sorts");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }

    public static int validateAnswer(int answer) {
        return answer;
    }

    private int partition(int[] array, int lo, int hi) {
        int pivot = array[hi];
        {
            int candidate2 = array[(hi - lo) * 2 / 3 + lo];
            int candidate1 = array[(hi - lo) / 3 + lo];
            if (pivot >= candidate1 && pivot <= candidate2)
                ;
            else if (candidate1 >= pivot && candidate1 <= candidate2)
                pivot = candidate1;
            else
                pivot = candidate2;
        }
        int i = lo;

        for (int j = lo; j < hi; j++) {
            Highlights.markArray(1, j);
            if (Reads.compareValues(array[j], pivot) < 0) {
                Writes.swap(array, i, j, 1, true, false);
                i++;
            }
            Delays.sleep(1);
        }
        Writes.swap(array, i, hi, 1, true, false);
        return i;
    }

    private void sortPart(int[] array, int lo, int hi) {
        {
            int firstValue = array[lo];
            for (int i = lo;; i++) {
                if (i > hi) {
                    return;
                }
                if (array[i] >= firstValue)
                    firstValue = array[i];
                else
                    break;
            }
        }
        int mid = (hi - lo) / 2 + lo;
        if (lo < hi) {
            int p = this.partition(array, lo, hi);
            int max = 1 << 20;
            int maxIdx = hi;
            while (p < mid) {
                max = 1 << 20;
                maxIdx = hi;
                for (int i = hi; i > p; i--) {
                    // Highlights.markArray(0, i);
                    if (Reads.compareIndexValue(array, i, max, 1.0 / 128.0, true) < 0) {
                        maxIdx = i;
                        max = array[i];
                    }
                }
                p++;
                Writes.swap(array, p, maxIdx, 1, true, false);
            }
            while (p > mid) {
                max = -(1 << 20);
                maxIdx = hi;
                for (int i = lo; i < p; i++) {
                    // Highlights.markArray(0, i);
                    if (Reads.compareIndexValue(array, i, max, 1.0 / 128.0, true) > 0) {
                        maxIdx = i;
                        max = array[i];
                    }
                }
                p--;
                Writes.swap(array, p, maxIdx, 1, true, false);
            }
            // p++;

            this.sortPart(array, lo, p - 1);
            this.sortPart(array, p + 1, hi);
        }
    }

    @Override
    public void runSort(int[] A, int length, int _unused) {
        sortPart(A, 0, length - 1);
        binaryInsertSort(A, 0, length, 0.1, 0.2);
    }
}
