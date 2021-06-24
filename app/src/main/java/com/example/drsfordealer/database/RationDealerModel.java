package com.example.drsfordealer.database;

public class RationDealerModel {

    private String dealerID;
    private String dealerName;
    private String dealerPassword;
    private String dealerLocationDistrict;
    private String dealerLocationMunicipality;
    private String dealerLocationState;
    private String dealerAccountRecoveryCode;

    public RationDealerModel( ) {

    }

    public RationDealerModel(String dealerID, String dealerName, String dealerPassword,
                             String dealerLocationDistrict, String dealerLocationMunicipality,
                             String dealerLocationState, String dealerAccountRecoveryCode) {
        this.dealerID = dealerID;
        this.dealerName = dealerName;
        this.dealerPassword = dealerPassword;
        this.dealerLocationDistrict = dealerLocationDistrict;
        this.dealerLocationMunicipality = dealerLocationMunicipality;
        this.dealerLocationState = dealerLocationState;
        this.dealerAccountRecoveryCode = dealerAccountRecoveryCode;
    }

    public String getDealerID() {
        return dealerID;
    }

    public void setDealerID(String dealerID) {
        this.dealerID = dealerID;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getDealerPassword() {
        return dealerPassword;
    }

    public void setDealerPassword(String dealerPassword) {
        this.dealerPassword = dealerPassword;
    }

    public String getDealerLocationDistrict() {
        return dealerLocationDistrict;
    }

    public void setDealerLocationDistrict(String dealerLocationDistrict) {
        this.dealerLocationDistrict = dealerLocationDistrict;
    }

    public String getDealerLocationMunicipality() {
        return dealerLocationMunicipality;
    }

    public void setDealerLocationMunicipality(String dealerLocationMunicipality) {
        this.dealerLocationMunicipality = dealerLocationMunicipality;
    }

    public String getDealerLocationState() {
        return dealerLocationState;
    }

    public void setDealerLocationState(String dealerLocationState) {
        this.dealerLocationState = dealerLocationState;
    }

    public String getDealerAccountRecoveryCode() {
        return dealerAccountRecoveryCode;
    }

    public void setDealerAccountRecoveryCode(String dealerAccountRecoveryCode) {
        this.dealerAccountRecoveryCode = dealerAccountRecoveryCode;
    }
}
