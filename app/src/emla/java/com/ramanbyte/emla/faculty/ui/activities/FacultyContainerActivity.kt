package com.ramanbyte.emla.faculty.ui.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ramanbyte.R
import com.ramanbyte.base.BaseActivity
import com.ramanbyte.databinding.ActivityFacultyContainerBinding
import com.ramanbyte.emla.base.di.authModuleDependency
import com.ramanbyte.emla.faculty.view_model.FacultyContainerViewModel
import com.ramanbyte.emla.ui.activities.ContainerActivity
import com.ramanbyte.utilities.AlertDialog

class FacultyContainerActivity : BaseActivity<ActivityFacultyContainerBinding,FacultyContainerViewModel>(
    authModuleDependency
) {

    companion object {
        fun intent(activity: Activity): Intent {
            return Intent(activity, FacultyContainerActivity::class.java)
        }
    }

    override val viewModelClass: Class<FacultyContainerViewModel> = FacultyContainerViewModel::class.java

    override fun layoutId(): Int = R.layout.activity_faculty_container

    override fun initiate() {

        AlertDialog(this, viewModel)

        layoutBinding.apply {
            lifecycleOwner = this@FacultyContainerActivity
            facultyContainerViewModel = viewModel

            viewModel.apply {

            }
        }
    }
}
