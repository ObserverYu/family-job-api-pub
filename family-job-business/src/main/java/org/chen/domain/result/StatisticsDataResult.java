package org.chen.domain.result;

import lombok.Data;

import java.util.List;

/**
 * 
 *  
 * @author ObserverYu
 * @date 2020/12/1 10:51
 **/

@Data
public class StatisticsDataResult {

    private List<StatisticsResult> showData;

    private List<StatisticsItemResult> itemList;

    private String title;


}
