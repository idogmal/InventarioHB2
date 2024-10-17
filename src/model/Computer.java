package model;

public class Computer {
    private String tag;           // Etiqueta TI
    private String model;         // Modelo
    private String brand;         // Marca
    private String state;         // Estado do Computador (novo, usado, etc.)
    private String userName;      // Nome do usuário
    private String serialNumber;  // Número de Série
    private String windowsVersion;// Versão do Windows
    private String officeVersion; // Versão do Office
    private String location;      // Localização
    private String purchaseDate;  // Data de Compra

    // Construtor
    public Computer(String tag, String model, String brand, String state, String userName, String serialNumber,
                    String windowsVersion, String officeVersion, String location, String purchaseDate) {
        this.tag = tag;
        this.model = model;
        this.brand = brand;
        this.state = state;
        this.userName = userName;
        this.serialNumber = serialNumber;
        this.windowsVersion = windowsVersion;
        this.officeVersion = officeVersion;
        this.location = location;
        this.purchaseDate = purchaseDate;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
