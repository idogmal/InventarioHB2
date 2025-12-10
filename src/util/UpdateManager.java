package util;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateManager {

    private static final String REMOTE_VERSION_URL = "https://raw.githubusercontent.com/idogmal/InventarioHB2/main/version.txt";

    private static String getAppJarName() {
        try {
            String path = UpdateManager.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File file = new File(path);
            if (file.isFile() && path.toLowerCase().endsWith(".jar")) {
                return file.getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "InventarioHB2.jar"; // Fallback default
    }

    public static void checkForUpdates(Component parent, String currentVersion) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                URL url = new URI(REMOTE_VERSION_URL).toURL();

                try (InputStream in = url.openStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

                    String remoteVersion = reader.readLine();
                    String downloadUrl = reader.readLine();

                    if (remoteVersion != null) {
                        String finalRemoteVersion = remoteVersion.trim();

                        if (isNewerVersion(currentVersion, finalRemoteVersion)) {
                            SwingUtilities.invokeLater(() -> showUpdateDialog(parent, finalRemoteVersion, downloadUrl));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Failed to check for updates: " + e.getMessage());
            }
        });
        executor.shutdown();
    }

    private static boolean isNewerVersion(String current, String remote) {
        // Simple string comparison for now, can be improved to semantic versioning
        // parsing
        // e.g. 1.0.0 vs 1.0.1
        return remote.compareTo(current) > 0;
    }

    private static void showUpdateDialog(Component parent, String newVersion, String downloadUrl) {
        String message = "Uma nova versão (" + newVersion + ") está disponível.\n" +
                "Deseja atualizar agora?";
        int option = JOptionPane.showConfirmDialog(parent, message, "Atualização Disponível",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            if (downloadUrl == null || downloadUrl.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "URL de download não encontrada.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            performUpdate(parent, downloadUrl);
        }
    }

    private static void performUpdate(Component parent, String downloadUrl) {
        ProgressMonitor progressMonitor = new ProgressMonitor(parent, "Baixando atualização...", "", 0, 100);
        progressMonitor.setMillisToDecideToPopup(0);
        progressMonitor.setMillisToPopup(0);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                URL url = new URI(downloadUrl).toURL();

                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
                connection.setInstanceFollowRedirects(true);

                int responseCode = connection.getResponseCode();

                if (responseCode == java.net.HttpURLConnection.HTTP_MOVED_PERM ||
                        responseCode == java.net.HttpURLConnection.HTTP_MOVED_TEMP) {
                    String newUrl = connection.getHeaderField("Location");
                    url = new URI(newUrl).toURL();
                    connection = (java.net.HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                    responseCode = connection.getResponseCode();
                }

                if (responseCode != java.net.HttpURLConnection.HTTP_OK) {
                    throw new IOException("Server returned HTTP " + responseCode + ": "
                            + connection.getResponseMessage() + "\nURL: " + url);
                }

                Path tempJar = Paths.get("update.jar");
                int fileSize = connection.getContentLength();

                try (InputStream in = connection.getInputStream();
                        FileOutputStream out = new FileOutputStream(tempJar.toFile())) {

                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    long totalRead = 0;

                    while ((bytesRead = in.read(buffer)) != -1) {
                        if (progressMonitor.isCanceled()) {
                            out.close();
                            Files.deleteIfExists(tempJar);
                            return;
                        }
                        out.write(buffer, 0, bytesRead);
                        totalRead += bytesRead;

                        if (fileSize > 0) {
                            int percent = (int) ((totalRead * 100) / fileSize);
                            progressMonitor.setProgress(percent);
                            progressMonitor.setNote("Baixado: " + (totalRead / 1024) + " KB");
                        }
                    }
                }

                progressMonitor.close();
                createAndRunBatchScript();

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(parent,
                        "Erro ao baixar atualização: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE));
            }
        });
        executor.shutdown();
    }

    private static void createAndRunBatchScript() {
        String jarName = getAppJarName();
        String script = "@echo off\r\n" +
                "timeout /t 2 /nobreak\r\n" +
                "del \"" + jarName + "\"\r\n" +
                "move /y update.jar \"" + jarName + "\"\r\n" +
                "start javaw -jar \"" + jarName + "\"\r\n" +
                "(goto) 2>nul & del \"%~f0\"\r\n";

        File scriptFile = new File("update.bat");
        try (PrintWriter writer = new PrintWriter(scriptFile)) {
            writer.print(script);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            new ProcessBuilder("cmd", "/c", "start", "update.bat").start();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
