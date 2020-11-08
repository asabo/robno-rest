package com.sunce.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansa.dao.net.InternalServerErrorException;

public class ZipUtil {
	private static Logger LOG = LogManager.getLogger(ZipUtil.class);


	public static final byte[] compress(byte[] content) {
		
		if (content == null)
			return null;

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				GZIPOutputStream gs = new GZIPOutputStream(baos);) {
			gs.write(content);
			gs.close(); //must be closed before getting bytes
			return baos.toByteArray();

		} catch (IOException e) {
			LOG.warn("Fatal error occured while compressing data:" + e);
			throw new com.ansa.dao.net.InternalServerErrorException("Problem while compressing incoming stream", e, 500);
		}

	}
	
	public static final byte[] uncompress(byte[] content) {
		
		if (content == null)
			return null;

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(content)) 
						) {
			byte[] buffer = new byte[1024];
			
			int len;
			while ((len = gis.read(buffer)) > 0) {
			    baos.write(buffer, 0, len);
			}

			gis.close();
			baos.close();
			byte[] bytes = baos.toByteArray();
			//LOG.info("Compressed data content: " + content.length + " uncompresed: " + bytes.length);
			return bytes;

		} catch (IOException e) {
			LOG.warn("Fatal error occured while decompressing data:" + e);
			throw new InternalServerErrorException("Problem while decompressing incoming stream", e, 500);
		}
	}	
}
