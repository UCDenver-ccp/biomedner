package bioner.process.common;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import bioner.data.document.BioNERDocument;
import bioner.data.document.BioNEREntity;
import bioner.data.document.BioNERSentence;
import bioner.global.GlobalConfig;
import bioner.process.BioNERProcess;

public class ProcessImpDebug implements BioNERProcess {


    String label;
	
	public ProcessImpDebug() {
        label = "";
	}
	public ProcessImpDebug(String label) {
        this.label = label;
	}
	
	@Override
	public void Process(BioNERDocument document) {
		// TODO Auto-generated method stub
		BioNERSentence[] sentences = document.getAllSentence();
        System.out.println("begin---------------------------- debug --- " + label);
		for (BioNERSentence sentence : sentences) {
			BioNEREntity[] entities = sentence.getAllEntities();
            if (entities.length > 0) {
			    for (BioNEREntity entity : entities) {
                    if (entity.getCandidates() == null) {
                        System.out.print(entity.getText() +  " null, ");
                    }
                    else {
                        System.out.print(entity.getText() +  " " + entity.getCandidates().length + ", ");
                    }
                }
                System.out.println("");
            }
        }
        System.out.println("end------------------ --- debug --- " + label);
	}
	
}
