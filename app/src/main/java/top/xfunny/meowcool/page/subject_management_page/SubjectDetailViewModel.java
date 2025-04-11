package top.xfunny.meowcool.page.subject_management_page;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import top.xfunny.meowcool.Application;
import top.xfunny.meowcool.core.data.SubjectNode;

public class SubjectDetailViewModel extends AndroidViewModel {
    public SubjectNode subjectNode;

    public SubjectDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void setSubjectNode(SubjectNode subjectNode){
        this.subjectNode = subjectNode;
    }

    public SubjectNode getSubjectNode(){
        return subjectNode;
    }



}
