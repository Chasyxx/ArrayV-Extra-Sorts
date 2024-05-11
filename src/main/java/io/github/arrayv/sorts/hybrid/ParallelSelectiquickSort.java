package io.github.arrayv.sorts.hybrid;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.BinaryInsertionSorting;

// SELECTIQUICK SORT
// A weird hybrid of quick sort where selection sort wants to make sure the partitions are of equal size
// and there's binary insertion sort at the end for some reason
// and this time with threads
// because threads

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
public class ParallelSelectiquickSort extends BinaryInsertionSorting {
    public ParallelSelectiquickSort(ArrayVisualizer arrayV) {
        super(arrayV);
        this.setSortListName("Parallel Selectiquick");
        this.setRunAllSortsName("Chasyxx's Parallel Selectiquick Sort");
        this.setRunSortName("Selectiquick Sort (Parallel)");
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

    private int[] a;

    private class SelectiquickSort extends Thread {
        private int lo, hi;

        SelectiquickSort(int lo, int hi) {
            this.lo = lo;
            this.hi = hi;
        }

        public void run() {
            ParallelSelectiquickSort.this.sortPart(this.lo, this.hi);
        }
    }

    private int partition(int lo, int hi) {
        int pivot = this.a[hi];
        {
            int candidate2 = this.a[(hi - lo) * 2 / 3 + lo];
            int candidate1 = this.a[(hi - lo) / 3 + lo];
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
            if (Reads.compareValues(this.a[j], pivot) < 0) {
                Writes.swap(this.a, i, j, 1, true, false);
                i++;
            }
            Delays.sleep(1);
        }
        Writes.swap(this.a, i, hi, 1, true, false);
        return i;
    }

    private void sortPart(int lo, int hi) {
        {
            int firstValue = this.a[lo];
            for (int i = lo;; i++) {
                if (i > hi) {
                    return;
                }
                if (this.a[i] >= firstValue)
                    firstValue = this.a[i];
                else
                    break;
            }
        }
        int mid = (hi - lo) / 2 + lo;
        if (lo < hi) {
            int p = this.partition(lo, hi);
            int max = 1 << 20;
            int maxIdx = hi;
            while (p < mid) {
                max = 1 << 20;
                maxIdx = hi;
                for (int i = hi; i > p; i--) {
                    // Highlights.markArray(0, i);
                    if (Reads.compareIndexValue(this.a, i, max, 1.0 / 128.0, true) < 0) {
                        maxIdx = i;
                        max = this.a[i];
                    }
                }
                p++;
                Writes.swap(this.a, p, maxIdx, 1, true, false);
            }
            while (p > mid) {
                max = -(1 << 20);
                maxIdx = hi;
                for (int i = lo; i < p; i++) {
                    // Highlights.markArray(0, i);
                    if (Reads.compareIndexValue(this.a, i, max, 1.0 / 128.0, true) > 0) {
                        maxIdx = i;
                        max = this.a[i];
                    }
                }
                p--;
                Writes.swap(this.a, p, maxIdx, 1, true, false);
            }
            // p++;

            SelectiquickSort left = new SelectiquickSort(lo, p - 1);
            SelectiquickSort right = new SelectiquickSort(p + 1, hi);
            left.start();
            right.start();

            try {
                left.join();
                right.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void runSort(int[] A, int length, int _unused) {
        this.a = A;
        sortPart(0, length - 1);
        binaryInsertSort(A, 0, length, 0.1, 0.2);
    }
}
