package crf.eval;

import java.io.File;

public class EvalRun {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CRFNEREvaluater evaluater = new CRFNEREvaluater();
		File rootDirFile = new File("J:/GENIA_Corp/test/big_result/");
		File[] files = rootDirFile.listFiles();
		for(File file : files)
		{
			if(file.getName().startsWith("result_"))
			{
				System.out.println(file.getName()+":");
				System.out.println("PRO");
				evaluater.evalFile(file.getAbsolutePath(), 23,22, "PRO");
				evaluater.PrintEvalResult();
				evaluater.clear();
				System.out.println("ORG");
				evaluater.evalFile(file.getAbsolutePath(), 23,22, "ORG");
				evaluater.PrintEvalResult();
				evaluater.clear();
				System.out.println("ALL");
				evaluater.evalFile(file.getAbsolutePath(), 23,22, "ALL");
				evaluater.PrintEvalResult();
				evaluater.clear();
				System.out.println();
			}
		}
	}

}
