package com.sunce.robno.rest.manager;

/**
 * <p>Title: Sunce*Robno</p>
 *
 * <p>Description: robno poslovanje</p>
 *
 * <p>Copyright: Copyright (c) 2008 Sunce mikrosustavi d.o.o.</p>
 *
 * <p>Company: Sunce mikrosustavi d.o.o.</p>
 *
 * @author Ante Sabo
 * @version 1.0
 */
public class Konstante
{
    public static String DEFAULT_DB_SERVER_ADRESA="ec2-3-248-231-37.eu-west-1.compute.amazonaws.com";//"172.30.66.212";
    public static String BAZA_PODATAKA="poslovnica";
    public static String BAZA_USERNAME="robno";
    public static String BAZA_PASSWORD="kramp222!";
    public static String BAZA_DEF_ENCODING="";//"?useUnicode=true&verifyServerCertificate=false&allowPublicKeyRetrieval=true";
    public static int MIN_DB_VEZA_POOL=1;
    public static int MAX_DB_VEZA_POOL=15;
    public static String DB_LOG_FILE="robno_db_pool_log.txt";

}
