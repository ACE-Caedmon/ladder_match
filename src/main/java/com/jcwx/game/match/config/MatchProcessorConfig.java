package com.jcwx.game.match.config;

import java.io.FileInputStream;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import com.jcwx.frm.config.Config;

public class MatchProcessorConfig implements Config{
	private MatchConfig matchConfig;
	@Override
	public void read(String path) throws Exception {
		InputStream input=new FileInputStream(path);;
		SAXReader saxReader = new SAXReader();
	    Document document=saxReader.read(input);
	   if(document.getRootElement()!=null){
		   this.matchConfig=readMatchConfig(document);
	   }
	   input.close();
	}

	public MatchConfig getMatchConfig() {
		return matchConfig;
	}

	private MatchConfig readMatchConfig(Document document){
		int maxUser=Integer.parseInt(document.selectSingleNode("//matchConfig/maxUser").getText());
		boolean campActive=Boolean.valueOf(document.selectSingleNode("//matchConfig/campActive").getText());
		long matchExecutePeriod=Long.valueOf(document.selectSingleNode("//matchConfig/matchExecutePeriod").getText());
		long matchPeriod=Long.valueOf(document.selectSingleNode("//matchConfig/matchRangeExpandConfig/period").getText());
		int incrementValue=Integer.parseInt(document.selectSingleNode("//matchConfig/matchRangeExpandConfig/incrementValue").getText());
		int maxValue=Integer.parseInt(document.selectSingleNode("//matchConfig/matchRangeExpandConfig/maxValue").getText());
		TRExpandConfig matchExpandConfig=new TRExpandConfig();
		matchExpandConfig.setPeriod(matchPeriod);
		matchExpandConfig.setIncrementValue(incrementValue);
		matchExpandConfig.setMaxValue(maxValue);
		MatchConfig config=new MatchConfig();
		if(document.selectSingleNode("//matchConfig/combineRangeExpandConfig")!=null){
			TRExpandConfig combineExpandConfig=new TRExpandConfig();
			long combinePeriod=Long.valueOf(document.selectSingleNode("//matchConfig/combineRangeExpandConfig/period").getText());
			int combineIncrementValue=Integer.parseInt(document.selectSingleNode("//matchConfig/combineRangeExpandConfig/incrementValue").getText());
			int combineMaxValue=Integer.parseInt(document.selectSingleNode("//matchConfig/combineRangeExpandConfig/maxValue").getText());
			combineExpandConfig.setIncrementValue(combineIncrementValue);
			combineExpandConfig.setPeriod(combinePeriod);
			combineExpandConfig.setMaxValue(combineMaxValue);
			config.setCombineExpandConfig(combineExpandConfig);
		}
		config.setCampActive(campActive);
		config.setMatchExecutePeriod(matchExecutePeriod);
		config.setMatchExpandConfig(matchExpandConfig);
		config.setMaxUser(maxUser);
		return config;
	}

}
