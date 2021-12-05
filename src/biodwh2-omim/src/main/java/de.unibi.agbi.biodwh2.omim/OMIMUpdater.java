package de.unibi.agbi.biodwh2.omim;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterMalformedVersionException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OMIMUpdater extends Updater<OMIMDataSource> {
    static final String FILE_GENEMAP2 = "genemap2.txt";


    private static final String OMIM_PAGE_URL = "https://www.omim.org/";
    private static final String DOWNLOAD_URL_mim2gene = "https://omim.org/static/omim/data/mim2gene.txt";
    private static final String mim2gene_FILENAME = "mim2gene.txt";
    private static final String mimTitles_FILENAME = "mimTitles.txt";
    private static final String genemap2_FILENAME = "genemap2.txt";
    private static final String morbidmap_FILENAME = "morbidmap.txt";
    private static final Pattern DOWNLOAD_URL_PATTERN = Pattern.compile("<h5>\\n.*Updated(.*)\\n.*<\\/h5>");

    public OMIMUpdater(final OMIMDataSource dataSource) {
        super(dataSource);
    }
    @Override
    public Version getNewestVersion() throws UpdaterException {
        try {
            final String html = HTTPClient.getWebsiteSource(OMIM_PAGE_URL);
            final Matcher matcher = DOWNLOAD_URL_PATTERN.matcher(html);
            String date = null;
            String date_split [];
            if (matcher.find())
                date = matcher.group(1);
                date_split = date.split(" ");
                String month = date_split[1];
                SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM");
                Calendar cal = Calendar.getInstance();
                cal.setTime(inputFormat.parse(month));
                SimpleDateFormat outputFormat = new SimpleDateFormat("MM"); // 01-12
                String month_number = outputFormat.format(cal.getTime());
                String[] day = date_split[2].split(",");

                return parseVersion(date_split[3] + "." + month_number + "." + day[0]);
        } catch (IOException | ParseException e) {
            throw new UpdaterConnectionException(e);
        }
    }


    private Version parseVersion(final String version) throws UpdaterMalformedVersionException {
        try {
            return Version.parse(version);
        } catch (NullPointerException | NumberFormatException e) {
            throw new UpdaterMalformedVersionException(version, e);
        }
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace) throws UpdaterException{
        final String mim2gene_dumpFilePath = dataSource.resolveSourceFilePath(workspace, mim2gene_FILENAME);
        final String mimTitles_dumpFilePath = dataSource.resolveSourceFilePath(workspace, mimTitles_FILENAME);
        final String genemap2_dumpFilePath = dataSource.resolveSourceFilePath(workspace, genemap2_FILENAME);
        final String morbidmap_dumpFilePath = dataSource.resolveSourceFilePath(workspace, morbidmap_FILENAME);

        final String downloadKey = dataSource.getProperties(workspace).get("downloadKey");
        final String downloadURL_mimTitles = "https://data.omim.org/downloads/" + downloadKey + "/mimTitles.txt";
        final String downloadURL_genemap2 = "https://data.omim.org/downloads/"+ downloadKey +"/genemap2.txt";
        final String downloadURL_morbidmap = "https://data.omim.org/downloads/"+ downloadKey+"/morbidmap.txt";
        downloadOmim(DOWNLOAD_URL_mim2gene,mim2gene_dumpFilePath);
        downloadOmim(downloadURL_mimTitles, mimTitles_dumpFilePath);
        downloadOmim(downloadURL_genemap2, genemap2_dumpFilePath);
        downloadOmim(downloadURL_morbidmap, morbidmap_dumpFilePath);
        return true;
    }

    private void downloadOmim(final String downloadUrl,final String dumpFilePath) throws UpdaterException {
        try {
            HTTPClient.downloadFileAsBrowser(downloadUrl, dumpFilePath);
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }
    @Override
    protected String[] expectedFileNames() {
        return new String[]{mim2gene_FILENAME, mimTitles_FILENAME, genemap2_FILENAME, morbidmap_FILENAME};
    }



}




