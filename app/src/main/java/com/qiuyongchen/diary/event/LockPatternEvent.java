package com.qiuyongchen.diary.event;

/**
 * Created by DELL on 2015/12/7.
 */
public class LockPatternEvent {
    private Boolean createSuccess;
    private Boolean comPareSuccess;
    private Boolean isCreating;

    public LockPatternEvent(Boolean createSuccess, Boolean comPareSuccess, Boolean isCreating) {
        this.createSuccess = createSuccess;
        this.comPareSuccess = comPareSuccess;
        this.isCreating = isCreating;
    }

    public Boolean getCreateSuccess() {
        return createSuccess;
    }

    public void setCreateSuccess(Boolean createSuccess) {
        this.createSuccess = createSuccess;
    }

    public Boolean getComPareSuccess() {
        return comPareSuccess;
    }

    public void setComPareSuccess(Boolean comPareSuccess) {
        this.comPareSuccess = comPareSuccess;
    }

    public Boolean getIsCreating() {
        return isCreating;
    }

    public void setIsCreating(Boolean isCreating) {
        this.isCreating = isCreating;
    }

}
