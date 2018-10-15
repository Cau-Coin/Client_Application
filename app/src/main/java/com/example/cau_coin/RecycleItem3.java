package com.example.cau_coin;

public class RecycleItem3 {
    String filter;

    public String getFilter() {
        return filter;
    }

    public String getFilter_noType(){
        if(filter.contains("학년")){
            return filter.substring(0,1);
        }
        else if(filter.contains("학기")){
            return filter.substring(0,1);
        }
        else{
            return filter;
        }
    }

    public String getFilter_Type(){
        if(filter.contains("학년")){
            return "학년";
        }
        else if(filter.contains("학기")){
            return "학기";
        }
        else{
            return filter;
        }
    }

    public RecycleItem3(String filter) {
        this.filter = filter;
    }
}
