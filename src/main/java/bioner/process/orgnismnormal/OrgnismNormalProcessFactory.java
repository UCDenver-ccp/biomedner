package bioner.process.orgnismnormal;



public class OrgnismNormalProcessFactory {
	public static OrgnismRecognizer createOrgnismRecognizer()
	{
		return new GeneralOrgnismNER();
	}
	public static OrgnismBasedNormalizer createOrgnismBasedNormalizer()
	{
		return new GeneralOrgnismBasedNormal();
	}
}
