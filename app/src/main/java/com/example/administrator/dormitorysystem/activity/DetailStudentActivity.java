package com.example.administrator.dormitorysystem.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.dormitorysystem.R;
import com.example.administrator.dormitorysystem.bean.Student;
import com.example.administrator.dormitorysystem.other.CacheManager;
import com.imnjh.imagepicker.SImagePicker;
import com.imnjh.imagepicker.activity.PhotoPickerActivity;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class DetailStudentActivity extends BaseActivity {

    @Bind(R.id.iv_personal_bg)
    ImageView ivPersonalBg;
    @Bind(R.id.cm_person)
    CircleImageView cmPerson;
    @Bind(R.id.et_true_name)
    EditText etTrueName;
    @Bind(R.id.et_student_sex)
    EditText etStudentSex;
    @Bind(R.id.et_grade)
    EditText etGrade;
    @Bind(R.id.et_className)
    EditText etClassName;
    @Bind(R.id.et_collega)
    EditText etCollega;
    @Bind(R.id.et_dorNum)
    EditText etDorNum;
    @Bind(R.id.et_dorDetaliNum)
    EditText etDorDetaliNum;
    @Bind(R.id.ll_information)
    LinearLayout llInformation;
    @Bind(R.id.tb_personal)
    Toolbar tbPersonal;
    @Bind(R.id.btn_student_ensure)
    Button btnStudentEnsure;
    public static final int REQUEST_CODE_AVATAR = 100;
    @Bind(R.id.et_telNum)
    EditText etTelNum;
    private String cmUrl;
    public static final String AVATAR_FILE_NAME = "avatar.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_student);
        ButterKnife.bind(this);
        setToolBar(R.id.tb_personal);
        Glide.with(this).load("http://bmob-cdn-13164.b0.upaiyun.com/2017/09/04/b1b8899cc0934c899bc86f88bafdf302.jpg").into(cmPerson);
    }

    @OnClick({R.id.cm_person, R.id.btn_student_ensure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cm_person:
                if (getCcamra() && getStorage()) {
                    SImagePicker
                            .from(this)
                            .pickMode(SImagePicker.MODE_AVATAR)
                            .showCamera(true)
                            .cropFilePath(
                                    CacheManager.getInstance().getImageInnerCache()
                                            .getAbsolutePath(AVATAR_FILE_NAME))
                            .forResult(REQUEST_CODE_AVATAR);
                } else {
                    Toast.makeText(mActivity, "请给予权限", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btn_student_ensure:
                if (!etClassName.getText().toString().isEmpty() && !etCollega.getText().toString().isEmpty() && !etDorDetaliNum.getText().toString().isEmpty() && !etDorNum.getText().toString().isEmpty()
                        && !etGrade.getText().toString().isEmpty() && !etTrueName.getText().toString().isEmpty() && !etStudentSex.getText().toString().isEmpty() && !etTelNum.getText().toString().isEmpty()) {
                    final Student student = Student.getCurrentUser(Student.class);
                    student.setName(etTrueName.getText().toString());
                    student.setClassName(etClassName.getText().toString());
                    student.setCollega(etCollega.getText().toString());
                    student.setDorDetaliNum(etDorDetaliNum.getText().toString());
                    student.setGrade(etGrade.getText().toString());
                    student.setMobilePhoneNumber(etTelNum.getText().toString());
                    student.setDorNum(etDorNum.getText().toString());
                    student.setSex(etStudentSex.getText().toString());
                    student.update(student.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(mActivity, "更新用户信息成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DetailStudentActivity.this,StudentActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("sInfo",student);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            } else if (e.getErrorCode() == 301){
                                 Toast.makeText(mActivity, "电话号码填写格式不对", Toast.LENGTH_SHORT).show();

                            }else if (e.getErrorCode() == 9016){
                                Toast.makeText(mActivity, "网络无连接( ▼-▼ )", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(mActivity, "更新用户信息失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(mActivity, "您有信息没有填完", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AVATAR) {
            final ArrayList<String> pathList =
                    data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT_SELECTION);
            Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
            options.width = 200;
            options.height = 200;
            showProgressDialog();
            Tiny.getInstance().source(pathList.get(0)).asFile().withOptions(options).compress(new FileCallback() {
                @Override
                public void callback(boolean isSuccess, String outfile) {
                    if (isSuccess) {
                        final BmobFile bmobFile = new BmobFile(new File(outfile));
                        bmobFile.uploadblock(new UploadFileListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    cmUrl = bmobFile.getUrl();
                                    updateHead();

                                    Toast.makeText(DetailStudentActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    dissmiss();
                                    Toast.makeText(DetailStudentActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        dissmiss();
                        Toast.makeText(DetailStudentActivity.this, "压缩失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void updateHead() {
        Student student = Student.getCurrentUser(Student.class);
        student.setNickUrl(cmUrl);
        student.update(student.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    dissmiss();
                    Toast.makeText(DetailStudentActivity.this, "更改成功", Toast.LENGTH_SHORT).show();
                    Glide.with(DetailStudentActivity.this)
                            .load(cmUrl)
                            .into(cmPerson);
                } else {
                    dissmiss();
                    Toast.makeText(DetailStudentActivity.this, "更改失败", Toast.LENGTH_SHORT).show();
                }
            }


        });
    }
}
