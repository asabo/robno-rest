package com.sunce.robno.rest.resource.v1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.ansa.dao.net.Artikl;
import com.ansa.dao.net.Korisnik;
import com.ansa.dao.net.Rezultat;
import com.sunce.robno.rest.RobnoRestApp;
import com.sunce.robno.rest.manager.StockManager;
import com.sunce.robno.rest.manager.UserAuthenticator;

@Path("v1/auth")
@Produces(MediaType.APPLICATION_JSON)
public final class RobnoRestV1 {
	private final static Logger LOG = Log.getLogger(RobnoRestV1.class);

	UserAuthenticator userAuthenticator;
	StockManager stockManager;

	@Inject
	public RobnoRestV1(UserAuthenticator userAuthenticator, StockManager stockManager) {
		this.userAuthenticator = userAuthenticator;
		this.stockManager = stockManager;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Korisnik logUser(Korisnik ulaz) { // UserCredentials creds

		Korisnik kor = userAuthenticator.authenticateUser(ulaz);
		if (kor == null)
			throw new NotAuthorizedException("Bad credentials");

		UUID uuid = RobnoRestApp.logUser(kor.getUserId());

		if (uuid == null) {
			throw new NotAuthorizedException("Bad credentials");
		}

		kor.setUsername(ulaz.getUsername());
		kor.setUuid(uuid);

		return kor;
	}

	@POST
	@Path("/changePass")
	@Consumes(MediaType.APPLICATION_JSON)
	public Boolean changePass(Korisnik ulaz) {

		Boolean rez = userAuthenticator.changePassword(ulaz);

		return rez;
	}

	@POST
	@Path("/stock")
	@Consumes(MediaType.APPLICATION_JSON)
	public Rezultat stock(Artikl artikl) {

		Rezultat rez = stockManager.getStockAvailbility(artikl.getSifArtikla(), artikl.getSifJedinicaMjere());

		return rez;
	}
	
	@POST
	@Path("/backup")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public byte[] backupData() {
		try {
			String komanda = "/usr/bin/mysqldump -urobno -pkramp222! --all-databases | gzip  > /home/ubuntu/db_bckp/robno-snap-latest.sql.gz";
			ProcessBuilder processBuilder = new ProcessBuilder();
			processBuilder.command("bash", "-c", komanda);
			
			Process process = processBuilder.start(); 
			
			LOG.info("backup process finished, result: " + process.waitFor());
			
			String datoteka = "robno-snap-latest.sql.gz";
			
			File f = new File("/home/ubuntu/db_bckp/" + datoteka);
			byte[] buffer = new byte[8096];
			
			try (
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			FileInputStream fins = new FileInputStream(f);
			) {
				int read;
				LOG.info("Idemo isokpirati podatke: " + f.getAbsolutePath());
				while ((read = fins.read(buffer)) != -1) {
					baos.write(buffer, 0, read);					
				}
				buffer = null; 
				baos.close();
				LOG.info(" iskopirali podatke: " + baos.size());
				
				return baos.toByteArray();
			}
		} catch (IOException | InterruptedException e) {
			LOG.warn("Problem running backup process: " + e);
			throw new InternalServerErrorException("Problem processing backup request");	
		}
	}	
}
