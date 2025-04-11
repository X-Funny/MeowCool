package top.xfunny.meowcool.page.subject_management_page;

import androidx.lifecycle.ViewModel;

import top.xfunny.meowcool.core.data.SubjectNode;

public class SubjectDetailViewModel extends ViewModel {
    public SubjectNode subjectNode;

    public void setSubjectNode(SubjectNode subjectNode){
        this.subjectNode = subjectNode;
    }

    public SubjectNode getSubjectNode(){
        return subjectNode;
    }



}
