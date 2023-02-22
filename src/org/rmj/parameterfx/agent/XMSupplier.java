/**
 * @author  Michael Cuison
 * @date    2018-04-19
 */
package org.rmj.parameterfx.agent;

import org.rmj.appdriver.constants.EditMode;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.iface.XMRecord;
import org.rmj.parameterfx.base.Supplier;
import org.rmj.parameterfx.pojo.UnitSupplier;

public class XMSupplier implements XMRecord{
    public XMSupplier(GRider foGRider, String fsBranchCD, boolean fbWithParent){
        this.poGRider = foGRider;
        if (foGRider != null){
            this.pbWithParent = fbWithParent;
            this.psBranchCd = fsBranchCD;
            
            poControl = new Supplier();
            poControl.setGRider(foGRider);
            poControl.setBranch(fsBranchCD);
            poControl.setWithParent(fbWithParent);
            
            pnEditMode = EditMode.UNKNOWN;
        }
    }
    
    @Override
    public void setMaster(int fnCol, Object foData) {
        if (pnEditMode != EditMode.UNKNOWN){
            // Don't allow specific fields to assign values
            if(!(fnCol == poData.getColumn("cRecdStat") ||
                fnCol == poData.getColumn("sModified") ||
                fnCol == poData.getColumn("dModified"))){
                
                if (fnCol == poData.getColumn("nDiscount") ||
                    fnCol == poData.getColumn("nCredLimt") ||
                    fnCol == poData.getColumn("nABalance")){
                    if (foData instanceof Number){
                        poData.setValue(fnCol, foData);
                    }else poData.setValue(fnCol, null);
                } else poData.setValue(fnCol, foData);   
            }
        }
    }

    @Override
    public void setMaster(String fsCol, Object foData) {
        setMaster(poData.getColumn(fsCol), foData);
    }

    @Override
    public Object getMaster(int fnCol) {
        if(pnEditMode == EditMode.UNKNOWN || poControl == null)
         return null;
      else{
         return poData.getValue(fnCol);
      }
    }

    @Override
    public Object getMaster(String fsCol) {
        return getMaster(poData.getColumn(fsCol));
    }

    @Override
    public boolean newRecord() {
        poData = poControl.newRecord();
        
        if (poData == null){
            ShowMessageFX();
            return false;
        }else{
            poData.setClientID("");
            poData.setBranchCode("");
            poData.setClientSince(poGRider.getSysDate());
            poData.setLedgerNo("0");
                    
            pnEditMode = EditMode.ADDNEW;
            return true;
        }
    }

    public boolean openRecord(String fsTransNox, String fsBranchCd) {
        poData = poControl.openRecord(fsTransNox, fsBranchCd);
        
        if (poData.getClientID() == null || poData.getBranchCode()== null){
            ShowMessageFX();
            return false;
        } else{
            pnEditMode = EditMode.READY;
            return true;
        }
    }

    @Override
    public boolean updateRecord() {
        if(pnEditMode != EditMode.READY) {
         return false;
      }
      else{
         pnEditMode = EditMode.UPDATE;
         return true;
      }
    }

    @Override
    public boolean saveRecord() {
        if(pnEditMode == EditMode.UNKNOWN){
            return false;
        }else{
            // Perform testing on values that needs approval here...
            UnitSupplier loResult;
            if(pnEditMode == EditMode.ADDNEW)
                loResult = poControl.saveRecord(poData, "", "");
            else loResult = poControl.saveRecord(poData, (String) poData.getValue(1), (String) poData.getValue(2));

            if(loResult == null){
                ShowMessageFX();
                return false;
            }else{
                pnEditMode = EditMode.READY;
                poData = loResult;
                return true;
            }
      }
    }

    public boolean deleteRecord(String fsTransNox, String fsBranchCd) {
        if(pnEditMode != EditMode.READY){
            return false;
        } else{
            boolean lbResult = poControl.deleteRecord(fsTransNox, fsBranchCd);
            if (lbResult)
                pnEditMode = EditMode.UNKNOWN;
            else ShowMessageFX();

            return lbResult;
        }
    }

    public boolean deactivateRecord(String fsTransNox, String fsBranchCd) {
        if(pnEditMode != EditMode.READY){
            return false;
        } else{
            boolean lbResult = poControl.deactivateRecord(fsTransNox, fsBranchCd);
            if (lbResult)
                pnEditMode = EditMode.UNKNOWN;
            else ShowMessageFX();

            return lbResult;
      }
    }

    public boolean activateRecord(String fsTransNox, String fsBranchCd) {
        if(pnEditMode != EditMode.READY){
            return false;
        } else{
            boolean lbResult = poControl.activateRecord(fsTransNox, fsBranchCd);
            if (lbResult)
                pnEditMode = EditMode.UNKNOWN;
            else ShowMessageFX();

            return lbResult;
        }
    }

    @Override
    public void setBranch(String foBranchCD) {
        psBranchCd = foBranchCD;
    }

    @Override
    public int getEditMode() {
        return pnEditMode;
    }
    
    //Added methods
    public void setGRider(GRider foGrider){
        this.poGRider = foGrider;
        this.psUserIDxx = foGrider.getUserID();
        
        if (psBranchCd.isEmpty()) psBranchCd = poGRider.getBranchCode();
    }
    
    private void ShowMessageFX(){
        if (!poControl.getErrMsg().isEmpty()){
            if (!poControl.getMessage().isEmpty())
                ShowMessageFX.Error(poControl.getErrMsg(), pxeModuleName, poControl.getMessage());
            else ShowMessageFX.Error(poControl.getErrMsg(), pxeModuleName, null);
        }else ShowMessageFX.Information(null, pxeModuleName, poControl.getMessage());
    }
    
    //Member Variables
    private GRider poGRider;
    private Supplier poControl;
    private UnitSupplier poData;
    
    private String psBranchCd;
    private int pnEditMode;
    private String psUserIDxx;
    private boolean pbWithParent;
    
    private final String pxeModuleName = "XMSupplier";

    @Override
    public boolean openRecord(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deleteRecord(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deactivateRecord(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean activateRecord(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
