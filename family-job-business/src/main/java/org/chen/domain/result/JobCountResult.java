package org.chen.domain.result;

import lombok.Data;

/**
 * 
 *  
 * @author ObserverYu
 * @date 2020/11/25 10:26
 **/

@Data
public class JobCountResult {
    Long myToCheck;
    Long myToFinish;
    Long myFinished;
    Long myExpired;
    Long myRefused;


    Long othersToCheck;
    Long othersToFinish;
    Long othersFinished;
    Long othersExpired;
    Long othersRefused;


}
