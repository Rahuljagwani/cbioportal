/** Copyright (c) 2012 Memorial Sloan-Kettering Cancer Center.
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center_
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center_
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.cbio.cgds.scripts;

import org.mskcc.cbio.cgds.dao.DaoClinical;
import org.mskcc.cbio.cgds.model.Clinical;
import org.mskcc.cbio.cgds.util.ProgressMonitor;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ImportClinical {
    private ProgressMonitor pMonitor;
    private File clincalFile;

    // our logger
    private static Log LOG = LogFactory.getLog(ImportClinical.class);

    // "commented out" string, i.e.metadata
    public static final String IGNORE_LINE_PREFIX = "#";

    public static String readCancerStudyId(String filename) throws IOException {

        FileReader metadata_f = new FileReader(filename);
        BufferedReader metadata = new BufferedReader(metadata_f);

        String line = metadata.readLine();
        while (line != null) {

            String[] fields = line.split(":");

            if (fields[0].trim().equals("cancer_study_identifier")) {
                return fields[1].trim();
            }

            line = metadata.readLine();
        }

        throw new IOException("cannot find cancer_study_identifier");
    }

    /**
     * reads a line from a clinical staging file given the ordering of the fields
     * @param line
     * @param colnames
     * @param delim
     * @return
     */
    public static HashMap<String, String> hashLine(String line, String[] colnames, String delim) {
        String[] lineSplit = line.split(delim);

        HashMap<String, String> toReturn = new HashMap<String, String>();

        if ( lineSplit.length != colnames.length ) {
            if (LOG.isInfoEnabled()) {
                LOG.info(lineSplit.length + " fields in line out of " + colnames.length + " of columns in file");
            }
        }

        for (int i = 0; i < colnames.length; i++) {
            toReturn.put( colnames[i], lineSplit[i] );
        }

        return toReturn;
    }

//    public static Clinical hashToClinical(HashMap<String, String> hash) {
//        for (Map.Entry<String, String> entry : hashedLine.entrySet()) {
//            if (entry.getKey().equals(caseIdColName)) {
//                continue;
//            }
//        }
//    }

    /**
     *
     * Import clinical data.
     *
     * Go over every attribute and check whether it exists in the db (clinical_attribute table).  If it exist, then
     * assume that it has been OKayed in the google doc.  If not, check the google doc.
     *
     * If it exists in the google doc and has been OKayed,
     * import it into the database.
     * If it exists in the google doc but has not been OKayed,
     * ignore.
     * If it does not exist in the google doc,
     * add it to the google doc with status "Unannotated" (but do not add to database)
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("command line usage:  importClinical.pl <clinical.txt> <metadata.txt>");
            System.exit(1);
        }

        FileReader clinical_f = new FileReader(args[0]);
        BufferedReader clinical = new BufferedReader(clinical_f);
        String line = clinical.readLine();
        String[] colnames = line.split("\t");

        // ASSUME : the first column in the column of case_ids
        String caseIdColName = colnames[0];

        line = clinical.readLine();
        while (line != null) {

            if (line.substring(0,1).equals(IGNORE_LINE_PREFIX)) {
                line = clinical.readLine();
                continue;
            }

            HashMap<String, String> hashedLine = hashLine(line, colnames, "\t");
//            hashToClinical(hashedLine);

            // go through everything in the hashmap
            // except for the case id
            // look for the clinicalAttribute
            // create it if it doesn't exist
            // return Clinical object with the correct ids and whatnot
            line = clinical.readLine();
        }

        // make a map of attributes to ClinicalAttribute objects

        // check to see whether or not the ClinicalAttribute is in the database, if not,
        // it should be and add it to the clinical attribute table.
    }
}
