package edu.gatech.ubicomp.continuousgestures.Learning;

import java.util.ArrayList;

// This code is referenced from Hong Xu
// May need to add claims for future distribution
// link: https://github.com/xuphys/peakdetect/blob/master/peakdetect.c
// Tidied by Chris Chow

public class PeakDetection
{
    private final static boolean DEBUG = false;
	public static int detect_peak
	(
			ArrayList<Double> data, /* data passed into function */
			int data_count, /* row count of data */
			ArrayList<Integer> peaks, /* emission peaks will be put here */
			// int[] num_emi_peaks, /* number of emission peaks found */
			int maxPeaks, /* maximum number of emission peaks */
			ArrayList<Integer> valleys, /* absorption peaks will be put here */
			// int[] num_absop_peaks, /* number of absorption peaks found */
			int maxValleys, /* maximum number of valley */
			double delta, /* delta used for distinguishing peaks */
			boolean peaksFirst /* should we search emission peak first or absorption peak first? */
	)
	{
		int i;
		double mx;
		double mn;
		int mx_pos = 0;
		int mn_pos = 0;
		boolean isDetectingEMI = peaksFirst;

		mx = data.get(0);
		mn = data.get(0);

		int numPeaks = 0;
		int numValleys = 0;

		for (i = 1; i < data_count; ++i)
		{
			if (data.get(i) > mx)
			{
				mx_pos = i;
				mx = data.get(i);
			}
			if (data.get(i) < mn)
			{
				mn_pos = i;
				mn = data.get(i);
			}

			if (isDetectingEMI)
			{
				if (data.get(i) < mx - delta)
				{
					if (numPeaks >= maxPeaks) /* not enough spaces */
					{
						// UGH return codes so nasty
						return 1;
					}
					
					// emi_peaks[num_emi_peaks] = mx_pos;
					numPeaks++;
					peaks.add(mx_pos);

					isDetectingEMI = false;

					i = mx_pos - 1;

					mn = data.get(mx_pos);
					mn_pos = mx_pos;
				}
			}
			else
			{
				if (data.get(i) > mn + delta)
				{
					if (numValleys >= maxValleys)
					{
						return 2;
					}
					// absop_peaks[num_absop_peaks] = mn_pos;
					numValleys++;
					valleys.add(mn_pos);
					
					isDetectingEMI = true;

					i = mn_pos - 1;

					mx = data.get(mn_pos);
					mx_pos = mn_pos;
				}
			}
		}

		return 0;
	}
}