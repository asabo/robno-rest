select distinct art.sifra,
    	 ifnull((select sum(p.kolicina) from poslovnica.primka_stavka p where p.sifra_artikl=a.sifra_artikl and p.sifra_jedinica_mjere = a.sifra_jedinica_mjere and p.indVal!='D'),0) as ulaz,
    	 ifnull((select sum(i.kolicina) from poslovnica.izdatnica_stavka i where i.sifra_artikl=a.sifra_artikl and i.sifra_jedinica_mjere = a.sifra_jedinica_mjere and i.indVal!='D'),0) as 'izlaz_skladiste',
    	 ifnull((select sum(r.kolicina) from poslovnica.racun_stavka r where r.sifra_artikl=a.sifra_artikl and r.sifra_jedinica_mjere = a.sifra_jedinica_mjere and r.indVal!='D' ),0) as 'izlaz_racuni'
    	 from poslovnica.primka_stavka a, katalog.artikl art, katalog.cjenik c,katalog.jedinica_mjere jm
    	 where c.sifra_artikl=a.sifra_artikl and a.sifra_artikl = art.sifra and jm.sifra=a.sifra_jedinica_mjere and art.roba='D'
    	 and a.sifra_artikl={sif_art} and a.sifra_jedinica_mjere={sif_jmj}
    	 group by a.sifra_artikl, a.sifra_jedinica_mjere, art.naziv, jm.naziv