package model;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Classe Computer representa um computador no sistema de inventário.
 */
public class Computer {
    private String tag;
    private String model;
    private String brand;
    private String state;
    private String userName;
    private String serialNumber;
    private String windowsVersion;
    private String officeVersion;
    private String purchaseDate;
    private String location; // Localidade do computador
    private String observation; // Observações do computador
    private String hostname; // Nome do PC
    private String sector; // Setor
    private String patrimony; // Patrimônio
    // Nota: O campo 'state' já existe (Novo, Usado). Este novo é para
    // Ativo/Inativo.
    // Vamos chamar de 'workingStatus' ou manter 'statusStr' para clareza interna,
    // mas getters/setters como getStatus.
    // Melhor: chamarei de 'activityStatus' para não confundir.

    private String activityStatus = "Ativo"; // Padrão: Ativo

    /**
     * Construtor vazio para Computer.
     */
    private int id; // ID único do banco de dados

    /**
     * Construtor vazio para Computer.
     */
    public Computer() {
        // Construtor vazio
    }

    /**
     * Construtor completo para inicializar todos os campos (sem ID, para novos
     * registros).
     */
    public Computer(String tag, String model, String brand, String state, String userName,
            String serialNumber, String windowsVersion, String officeVersion, String purchaseDate, String location,
            String observation, String hostname, String sector, String patrimony, String activityStatus) {
        this.tag = tag;
        this.model = model;
        this.brand = brand;
        this.state = state;
        this.userName = userName;
        this.serialNumber = serialNumber;
        this.windowsVersion = windowsVersion;
        this.officeVersion = officeVersion;
        this.purchaseDate = purchaseDate;
        this.location = (location != null && !location.trim().isEmpty()) ? location.trim() : "Desconhecido";
        this.observation = observation;
        this.hostname = hostname;
        this.sector = sector;
        this.patrimony = patrimony;
        this.activityStatus = (activityStatus != null && !activityStatus.isEmpty()) ? activityStatus : "Ativo";
    }

    // Construtor de compatibilidade (sem novos campos e sem observation)
    public Computer(String tag, String model, String brand, String state, String userName,
            String serialNumber, String windowsVersion, String officeVersion, String purchaseDate, String location) {
        this(tag, model, brand, state, userName, serialNumber, windowsVersion, officeVersion, purchaseDate, location,
                "", "", "", "", "Ativo");
    }

    /**
     * Construtor completo com ID (para registros carregados do banco).
     */
    public Computer(int id, String tag, String model, String brand, String state, String userName,
            String serialNumber, String windowsVersion, String officeVersion, String purchaseDate, String location,
            String observation, String hostname, String sector, String patrimony, String activityStatus) {
        this.id = id;
        this.tag = tag;
        this.model = model;
        this.brand = brand;
        this.state = state;
        this.userName = userName;
        this.serialNumber = serialNumber;
        this.windowsVersion = windowsVersion;
        this.officeVersion = officeVersion;
        this.purchaseDate = purchaseDate;
        this.location = (location != null && !location.trim().isEmpty()) ? location.trim() : "Desconhecido";
        this.observation = observation;
        this.hostname = hostname;
        this.sector = sector;
        this.patrimony = patrimony;
        this.activityStatus = (activityStatus != null && !activityStatus.isEmpty()) ? activityStatus : "Ativo";
    }

    // Construtor de compatibilidade com ID (sem novos campos e sem observation)
    public Computer(int id, String tag, String model, String brand, String state, String userName,
            String serialNumber, String windowsVersion, String officeVersion, String purchaseDate, String location) {
        this(id, tag, model, brand, state, userName, serialNumber, windowsVersion, officeVersion, purchaseDate,
                location, "", "", "", "", "Ativo");
    }

    // Getters e Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getWindowsVersion() {
        return windowsVersion;
    }

    public void setWindowsVersion(String windowsVersion) {
        this.windowsVersion = windowsVersion;
    }

    public String getOfficeVersion() {
        return officeVersion;
    }

    public void setOfficeVersion(String officeVersion) {
        this.officeVersion = officeVersion;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location != null ? location : "Desconhecido";
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getPatrimony() {
        return patrimony;
    }

    public String getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(String activityStatus) {
        this.activityStatus = activityStatus;
    }

    private boolean isDeleted; // Flag para exclusão lógica

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public void setPatrimony(String patrimony) {
        this.patrimony = patrimony;
    }

    /**
     * Calcula o tempo de uso do computador com base na data de compra.
     * Retorna uma string formatada (ex: "2a 5m") ou "-" se inválido.
     */
    public String getUsageTime() {
        if (purchaseDate == null || purchaseDate.trim().isEmpty() || purchaseDate.contains("_")) {
            return "-";
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate purchase = LocalDate.parse(purchaseDate, formatter);
            LocalDate now = LocalDate.now();

            if (purchase.isAfter(now)) {
                return "0m"; // Data futura
            }

            Period period = Period.between(purchase, now);
            int years = period.getYears();
            int months = period.getMonths();

            if (years > 0) {
                return years + "a " + months + "m";
            } else {
                return months + "m";
            }
        } catch (DateTimeParseException e) {
            return "-";
        }
    }

    /**
     * Retorna o tempo de uso detalhado para tooltip.
     * Ex: "2 anos, 5 meses e 12 dias"
     */
    public String getDetailedUsageTime() {
        if (purchaseDate == null || purchaseDate.trim().isEmpty() || purchaseDate.contains("_")) {
            return "Data de compra não informada";
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate purchase = LocalDate.parse(purchaseDate, formatter);
            LocalDate now = LocalDate.now();

            if (purchase.isAfter(now)) {
                return "Data de compra futura";
            }

            Period period = Period.between(purchase, now);
            int years = period.getYears();
            int months = period.getMonths();
            int days = period.getDays();

            StringBuilder sb = new StringBuilder();
            if (years > 0)
                sb.append(years).append(years == 1 ? " ano, " : " anos, ");
            if (months > 0)
                sb.append(months).append(months == 1 ? " mês e " : " meses e ");
            sb.append(days).append(days == 1 ? " dia" : " dias");

            return sb.toString();
        } catch (DateTimeParseException e) {
            return "Data inválida";
        }
    }

    @Override
    public String toString() {
        return "Computer{" +
                "tag='" + tag + '\'' +
                ", model='" + model + '\'' +
                ", brand='" + brand + '\'' +
                ", state='" + state + '\'' +
                ", userName='" + userName + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", windowsVersion='" + windowsVersion + '\'' +
                ", officeVersion='" + officeVersion + '\'' +
                ", purchaseDate='" + purchaseDate + '\'' +
                ", location='" + location + '\'' +
                ", hostname='" + hostname + '\'' +
                ", sector='" + sector + '\'' +
                ", patrimony='" + patrimony + '\'' +
                ", activityStatus='" + activityStatus + '\'' +
                '}';
    }
}
