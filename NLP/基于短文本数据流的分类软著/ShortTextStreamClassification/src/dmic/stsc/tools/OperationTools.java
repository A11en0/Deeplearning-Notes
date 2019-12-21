package dmic.stsc.tools;

public class OperationTools 
{

	public static void add2DimArr(double[][] A, double[][] B) 
	{
		for (int i=0; i<A.length; i++)
		{
			for(int j=0; j<A[i].length; j++)
			{
				A[i][j] += B[i][j];
			}
		}
		
	}
	
	public static double[] addColumn(double[][] A) 
	{
		double[] cA = new double[A.length];
		for (int i=0; i<A.length; i++)
		{
			for(int j=0; j<A[i].length; j++)
			{
				cA[i] += A[i][j];
			}
		}
		return cA;
	}
	
	public static void zeroToOne2(double[][] A)
	{
		for (int i=0; i<A.length; i++)
		{
			for (int j=0; j<A[i].length; j++)
			{
				A[i][j]=(A[i][j]==0.0) ? 1 : A[i][j];
			}
		}
		
	}
	
	public static void zeroToOne1(double[] A)
	{
		for (int i=0; i<A.length; i++)
		{
			A[i]=(A[i]==0.0) ? 1 : A[i];
		}
		
	}
	
	public static void division(double[][] A, double[] B)
	{
		for (int i=0; i<A.length; i++)
		{
			for (int j=0; j<A[i].length; j++)
			{
				A[i][j] /= B[i];
			}
		}
	}
	
	public static double maxValue(double[] arr) 
	{
		double maxValue = Double.MIN_VALUE;
		int inx = -1;
		for (int i=0; i<arr.length; i++)
		{
			if (maxValue < arr[i])
			{
				maxValue = arr[i];
				inx = i;
			}
		}
		return maxValue;
	}
	
	public static double minValue(double[] arr) 
	{
		double minValue = Double.MIN_VALUE;
		int inx = -1;
		for (int i=0; i<arr.length; i++)
		{
			if (minValue > arr[i])
			{
				minValue = arr[i];
				inx = i;
			}
		}
		return minValue;
	}
	
	public static double maxInx(double[] arr) 
	{
		double maxV = Double.MIN_EXPONENT;
		double inx = 0;
		for (int i=0; i<arr.length; i++)
		{
			if (maxV<arr[i])
			{
				maxV = arr[i];
				inx = i;
			}
		}
		return inx;
	}
	
	public static int minInx(double[] arr) 
	{
		double minV = Double.MAX_VALUE;
		int inx = 0;
		for (int i=0; i<arr.length; i++)
		{
			if (minV>arr[i])
			{
				minV = arr[i];
				inx = i;
			}
		}
		return inx;
	}
}
