package model;

public class Computer {
    private String tag;
    private String serialNumber;
    private String model;
    private String brand;
    private String state;
    private String userName;
    private String windowsVersion;
    private String officeVersion;
    private String location;
    private String purchaseDate;

    public Computer(String tag, String serialNumber, String model, String brand, String state, String userName,
                    String windowsVersion, String officeVersion, String location, String purchaseDate) {
        this.tag = tag;
        this.serialNumber = serialNumber;
        this.model = model;
        this.brand = brand;
        this.state = state;
        this.userName = userName;
        this.windowsVersion = windowsVersion;
        this.officeVersion = officeVersion;
        this.location = location;
        this.purchaseDate = purchaseDate;
    }

    // Getters para acessar os campos
    public String getTag() {
        return tag;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getModel() {
        return model;
    }

    public String getBrand() {
        return brand;
    }

    public String getState() {
        return state;
    }

    public String getUserName() {
        return userName;
    }

    public String getWindowsVersion() {
        return windowsVersion;
    }

    public String getOfficeVersion() {
        return officeVersion;
    }

    public String getLocation() {
        return location;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }
}
