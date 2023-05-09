package com.carterz30cal.utils;

public class MathsUtils
{
	public static double getCircleX(double degrees)
	{
		return Math.sin(Math.toRadians(degrees));
	}
	public static double getCircleZ(double degrees)
	{
		return Math.cos(Math.toRadians(degrees));
	}
}
