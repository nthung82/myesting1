package com.mydomain.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

/**
 * Dummy test class
 * 
 * @author Aykut//////////
 * 
 */

@Controller
@RequestMapping("/dummy")
public class DummyController {

    @RequestMapping(method = RequestMethod.GET)
  
    public String test() {
  
        return "index";
    }
    //------------
    private ArrayList<ObjectData> mytest(){
    	  Connection con = null;
          PreparedStatement pst = null;
          ResultSet rs = null;
          ArrayList<ObjectData> list= new ArrayList<>();
          try {
              
              con = Test.ConnectDatabase();
              pst = con.prepareStatement("SELECT * FROM gebruiker");
              rs = pst.executeQuery();
ObjectData obj;
              while (rs.next()) {
            	  obj = new ObjectData();
            	  obj.setId(rs.getInt(1));
            	  obj.setPuik_id(rs.getString(3));
            	  obj.setDisplaynaam(rs.getString(2));
                  list.add(obj);
              }

          } catch (SQLException ex) {
                

          } finally {

              try {
                  if (rs != null) {
                      rs.close();
                  }
                  if (pst != null) {
                      pst.close();
                  }
                  if (con != null) {
                      con.close();
                  }

              } catch (SQLException ex) {
                
              }
          }
      return list;
    }
    //-------------
    @RequestMapping(value = "/jasper")
    public void generatePDFJasperChart(HttpServletRequest request,
    			HttpServletResponse response) throws IOException {
     
    	String sourceFileName = "D:/test.jrxml";
    	System.out.println(sourceFileName);
    	Map<String, Object> parameters = new HashMap<String, Object>();
    	parameters.put("ReportTitle", "Jasper Demo");
    	parameters.put("Author", "Prepared By jCombat");
    	try {
    		System.out.println("Start compiling!!! ...");
    		JasperCompileManager.compileReportToFile(sourceFileName);
    		System.out.println("Done compiling!!! ...");
    	//	sourceFileName = "D:/test.jasper";
    		
    		ArrayList<ObjectData> dataList = mytest();
//    		/ create a new Gson instance
    		 Gson gson = new Gson();
    		 // convert your list to json
    		 String jsonCartList = gson.toJson(dataList);
    		 System.err.println(jsonCartList);
    		/*JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(
    				dataList);*/
    		InputStream stream = new ByteArrayInputStream(jsonCartList.getBytes("UTF-8"));
    		JsonDataSource xx= new JsonDataSource(stream);
    	//	JasperReport jasperReport = JasperCompileManager.compileReport("D:/test.jrxml");
    	
    		JasperPrint jasperPrint = JasperFillManager.fillReport("D:/test.jasper",
    				parameters, xx);
    		if (jasperPrint != null) {
    			byte[] pdfReport = JasperExportManager
    					.exportReportToPdf(jasperPrint);
    			response.reset();
    			response.setContentType("application/pdf");
    			response.setHeader("Cache-Control", "no-store");
    			response.setHeader("Content-Disposition", "attachment; filename=your-file.pdf");
    			response.setHeader("Cache-Control", "private");
    			response.setHeader("Pragma", "no-store");
    			response.setContentLength(pdfReport.length);
    			response.getOutputStream().write(pdfReport);
    			response.getOutputStream().flush();
    			response.getOutputStream().close();
    		}
    	} catch (JRException e) {
    		e.printStackTrace();
    	}
    }
}
