package io.github.arrayv.sorts.exchange;

import io.github.arrayv.main.ArrayVisualizer;
import io.github.arrayv.sorts.templates.Sort;

// SHIFT SORT
// An attempt at making Stalin sort an actual sort gone awry

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

public final class ShiftSort extends Sort {
    public ShiftSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Shift");
        this.setRunAllSortsName("Chasyxx's Shift Sort");
        this.setRunSortName("Shiftsort");
        this.setCategory("Exchange Sorts");
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }

    @Override
    public void runSort(int[] array, int length, int _extra) {
        int endIdx = length;
        int squareRoot = (int) Math.sqrt(length);
        int subSortSize = (int) (Math.max(Math.ceil(squareRoot * 20.0 / 64.0), 5));
        int initialPasses = (int) (Math.floor(length / 1024));
        {
            arrayVisualizer.toggleAnalysis(true);
            int whole = 0;
            boolean sorted = true;
            int H1 = 0;
            int H2 = 0;
            for (int i = 0; i < (length / 2); i++) {
                if (Reads.compareIndices(array, i, i + 1, 1, true) < 0) {
                    whole++;
                    H1++;
                } else if (Reads.compareIndices(array, i, i + 1, 0, false) > 0) {
                    whole--;
                    H1--;
                    sorted = false;
                }
            }
            for (int i = (length / 2); i < length; i++) {
                if (Reads.compareIndices(array, i, i + 1, 1, true) < 0) {
                    whole++;
                    H2++;
                } else if (Reads.compareIndices(array, i, i + 1, 0, false) > 0) {
                    whole--;
                    H2--;
                    sorted = false;
                }
            }
            arrayVisualizer.toggleAnalysis(false);
            if (sorted)
                return;
            if (whole < 0) {
                Writes.reversal(array, 0, length - 1, 1, true, false);
                whole = -whole;
                int temp = -H1;
                H1 = -H2;
                H2 = temp;
            }
            if (H1 < 0) {
                Writes.reversal(array, 0, (int) (Math.round(length / 2.0)) - 1, 1, true, false);
                H1 = -H1;
            }
            if (H2 < 0) {
                Writes.reversal(array, (int) (Math.round(length / 2.0)), length - 1, 1, true, false);
                H2 = -H2;
            }
            if (H1 >= (length / 4) && H2 >= (length / 4)) {
                if (length > 1024) {
                    int[] temp = Writes.createExternalArray(length / 2);
                    for (int i = 0; i < (length / 2); i++)
                        Writes.write(temp, i, array[i], 5, true, true);
                    for (int i = 0; i < (length / 2); i++)
                        Writes.swap(array, i + (length / 2), i * 2 + 1, 5, true, false);
                    for (int i = 0; i < (length / 2); i++)
                        Writes.write(array, i * 2, temp[i], 5, true, false);
                    Writes.deleteExternalArray(temp);
                } else
                    for (int i = 0; i < (Math.round(length / 2.0)); i++) {
                        Writes.multiSwap(array, i + (int) (Math.round(length / 2.0)), i * 2, 0.01, true, false);
                    }
            }
        }
        while (true) {
            int sr2 = squareRoot * 4;
            if (endIdx > sr2)
                for (int k = 0; k < Math.max(1, (int) (Math.sqrt(length/256))); k++)
                    for (int i = 0; i < endIdx - sr2; i++) {
                        for (int j = 0; j < sr2; j += (squareRoot < 24) ? 2 : 3) {
                            if (Reads.compareIndices(array, i + j, i + j + 1, 0, true) > 0) {
                                Writes.swap(array, i + j, i + j + 1, 0, true, false);
                            }
                        }
                        Delays.sleep(2);
                    }
            boolean didSomething = false;
            int lastDidSomethingAt = 0;
            int bufferStartIdx = 0;
            int max = Integer.MIN_VALUE;
            for (int i = 0; i < endIdx;) {
                while (true) {
                    boolean subDidSomething = false;
                    for (int j = 0; j < subSortSize; j++)
                        if ((i + j + 1) < length)
                            if (Reads.compareIndices(array, i + j, i + j + 1, 0.1, true) > 0) {
                                Writes.swap(array, i + j, i + j + 1, 0.1, true, false);
                                subDidSomething = true;
                            }
                    if (!subDidSomething)
                        break;
                }
                Highlights.markArray(1, i);
                Delays.sleep(0.5);
                if (Reads.compareValues(max, array[i]) > 0) {
                    if (initialPasses>0)
                        Writes.swap(array, i, bufferStartIdx, 1, true, false);
                    else
                        Writes.multiSwap(array, i, bufferStartIdx, 0.1, true, false);
                    bufferStartIdx++;
                    didSomething = true;
                    lastDidSomethingAt = i;
                } else {
                    max = array[i];
                }
                i++;
            }
            if (!didSomething)
                break;
            endIdx = lastDidSomethingAt;
            Highlights.markArray(3, endIdx);
            initialPasses--;
        }
    }
}
