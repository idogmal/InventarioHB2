package model;

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

    /**
     * Construtor vazio para Computer.
     */
    public Computer() {
        // Construtor vazio
    }

    /**
     * Construtor completo para inicializar todos os campos.
     */
    public Computer(String tag, String model, String brand, String state, String userName,
                    String serialNumber, String windowsVersion, String officeVersion, String purchaseDate, String location) {
        this.tag = tag;
        this.model = model;
        this.brand = brand;
        this.state = state;
        this.userName = userName;
        this.serialNumber = serialNumber;
        this.windowsVersion = windowsVersion;
        this.officeVersion = officeVersion;
        this.purchaseDate = purchaseDate;
        this.location = location != null ? location : "Desconhecido";
    }

    // Getters e Setters

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
                '}';
    }
}
