package com.forms.dhttp.dbean;

import com.forms.dhttp.base.RootRsp;

import java.util.List;

/**
 * Created by bubbly on 2017/8/3.
 */

public class EntryBean extends RootRsp {

    private String jsonVersion;
    private String funcListVersion;
    private String tabBarVersion;
    private List<FunctionBean> funcList;

    public String getJsonVersion() {
        return jsonVersion;
    }

    public void setJsonVersion(String jsonVersion) {
        this.jsonVersion = jsonVersion;
    }

    public String getFuncListVersion() {
        return funcListVersion;
    }

    public void setFuncListVersion(String funcListVersion) {
        this.funcListVersion = funcListVersion;
    }

    public String getTabBarVersion() {
        return tabBarVersion;
    }

    public void setTabBarVersion(String tabBarVersion) {
        this.tabBarVersion = tabBarVersion;
    }

    public List<FunctionBean> getFuncList() {
        return funcList;
    }

    public void setFuncList(List<FunctionBean> funcList) {
        this.funcList = funcList;
    }
}
