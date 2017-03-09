package com.zhouyuhao.ftpcomputer.ftp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

/**
 * 泰隆银行计算器
 * @author zhouyuhao01
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int MONTH = 30;
    public static final int YEAR = 365;


    /**
     * 开始时间弹窗
     */
    private AlertDialog mDatePickerDialogStart;
    /**
     * 结束时间弹窗
     */
    private AlertDialog mDatePickerDialogEnd;

    /**
     * 业务金额
     */
    private EditText mEditTextMoneyCount;

    /**
     * 贷款利率
     */
    private EditText mEditTextRate;

    /**
     * 当前的开始时间
     */
    private Button mTextViewStartTime;
    /**
     * 当前的结束时间
     */
    private Button mTextViewEndTime;
    /**
     * 还款模式
     */
    private RadioGroup mRadioGroupRepayMode;
    /**
     * 开始计算按钮
     */
    private Button mButtonCompute;
    /**
     * 利润
     */
    private TextView mTextBenefit;
    /**
     * ftp利率
     */
    private TextView mTextFTPRate;

    private DatePicker mDatePickerStart;
    private DatePicker mDatePickerEnd;

    /** 当前贷款天数 */
    private int mPayDayCounts = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mEditTextMoneyCount = (EditText) findViewById(R.id.edit_money_count);
        mEditTextRate = (EditText) findViewById(R.id.edit_money_rate);

        mTextViewEndTime = (Button) findViewById(R.id.datepicker_end);
        mTextViewEndTime.setOnClickListener(this);

        mTextViewStartTime = (Button) findViewById(R.id.datepicker_start);
        mTextViewStartTime.setOnClickListener(this);
        mRadioGroupRepayMode = (RadioGroup) findViewById(R.id.radio_repayment_method);
        mButtonCompute = (Button) findViewById(R.id.button_computer);
        mButtonCompute.setOnClickListener(this);

        mTextBenefit = (TextView) findViewById(R.id.text_benefit);

        mTextFTPRate =(TextView) findViewById(R.id.text_ftp_rate);

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        // 日历
        Calendar calendar = Calendar.getInstance();

        mDatePickerDialogStart =  new DatePickerDialog(MainActivity.this,
                // 绑定监听器
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mDatePickerStart = view;
                        TextView show = (TextView) findViewById(R.id.datepicker_start);
                        show.setText(view.getYear() + "年" + (view.getMonth() + 1) + "月" + view.getDayOfMonth() + "日");

                        calculateDays();
                        if (mPayDayCounts > 0) {
                            mTextFTPRate.setText(calculateRate(mPayDayCounts) + "%");
                        }

                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        mDatePickerDialogEnd =  new DatePickerDialog(MainActivity.this,
                // 绑定监听器
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mDatePickerEnd = view;
                        TextView show = (TextView) findViewById(R.id.datepicker_end);
                        show.setText(view.getYear() + "年" + (view.getMonth() + 1) + "月" + view.getDayOfMonth() + "日");
                        calculateDays();
                        if (mPayDayCounts > 0) {
                            mTextFTPRate.setText(calculateRate(mPayDayCounts) + "%");
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

    }

    /**
     * 计算利润
     */
    private void compute() {

        float money;
        float rate;

        try {
            if (mEditTextMoneyCount.getText() != null && mEditTextMoneyCount.getText().length() > 0) {
                money = Float.valueOf(mEditTextMoneyCount.getText().toString()); // 当前的钱
            } else {
                Toast.makeText(this, "请输入金额", Toast.LENGTH_LONG).show();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(this, "请输入正确金额格式", Toast.LENGTH_LONG).show();
            return;
        }


        try {
            if (mEditTextRate.getText() != null && mEditTextRate.getText().length() > 0) {
                rate = Float.valueOf(mEditTextRate.getText().toString()); // 当前利率
            } else {
                Toast.makeText(this, "请输入利率", Toast.LENGTH_LONG).show();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(this, "请输入正确利率格式", Toast.LENGTH_LONG).show();
            return;
        }

        if (mDatePickerStart == null) {
            Toast.makeText(this, "请选择开始时间", Toast.LENGTH_LONG).show();
            return;
        }

        if (mDatePickerEnd == null) {
            Toast.makeText(this, "请选择结束时间", Toast.LENGTH_LONG).show();
            return;
        }

        int days = mPayDayCounts;
        if (days < 30) {
            days = 30;
        }
        double benefit = money * days * ((rate - calculateRate(mPayDayCounts)) / 100) / 365 ;

        mTextBenefit.setText(String.format("%.2f", benefit * 10000) + "元");

    }

    private void calculateDays() {

        if (mDatePickerStart != null && mDatePickerEnd != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(mDatePickerStart.getYear(), mDatePickerStart.getMonth(), mDatePickerStart.getDayOfMonth());
            long startTime  = calendar.getTimeInMillis();
            calendar.set(mDatePickerEnd.getYear(), mDatePickerEnd.getMonth(), mDatePickerEnd.getDayOfMonth());
            long endTime = calendar.getTimeInMillis();
            mPayDayCounts = (int) ((endTime - startTime) / (1000*3600*24));
        }
    }

    private float calculateRate(int payDayCount) {
        float ftpRate;
        if (payDayCount < MONTH * 3) {
            ftpRate = 4.296f;
        } else if (payDayCount < MONTH * 6) {
            ftpRate = 4.856f;
        } else if (payDayCount < MONTH * 9) {
            ftpRate = 6.378f;
        } else if (payDayCount < YEAR) {
            ftpRate = 5.768f;
        } else if (payDayCount < YEAR * 2) {
            ftpRate = 6.009f;
        } else if (payDayCount < YEAR * 3) {
            ftpRate = 7.1f;
        } else {
            ftpRate = 7.27f;
        }
        return ftpRate;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.datepicker_end:
                mDatePickerDialogEnd.show();
                break;
            case R.id.datepicker_start:
                mDatePickerDialogStart.show();
                break;
            case R.id.button_computer:
                compute();
                break;
        }
    }
}
