# keyboard
自定义数字键盘和字母键盘，包含两种数字键盘，字母键盘，字母密码键盘，数字密码键盘等。
## 引入方式

```groovy
// 引入键盘库
implementation 'com.xzy.util:keyboard:1.0.2'
```

## 使用方式



```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:keyboard="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <RelativeLayout
        android:id="@+id/activity_main1"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:padding="80dp">

        <com.xzy.keyboard.KeyboardEditText
            android:id="@+id/et_input_1"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="数字"
            android:textSize="80sp"
            keyboard:inputType="number" />

        <com.xzy.keyboard.KeyboardEditText
            android:id="@+id/et_input_2"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_below="@+id/et_input_1"
            android:hint="数字2"
            android:textSize="80sp"
            keyboard:inputType="number2" />

        <com.xzy.keyboard.KeyboardEditText
            android:id="@+id/et_input_3"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_below="@+id/et_input_2"
            android:layout_alignParentStart="true"
            android:hint="字母"
            android:textSize="80sp"
            keyboard:inputType="qwerty" />

        <com.xzy.keyboard.KeyboardEditText
            android:id="@+id/et_input_4"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_below="@+id/et_input_3"
            android:layout_alignParentStart="true"
            android:hint="字母密码"
            android:textSize="80sp"
            keyboard:inputType="qwerty"
            keyboard:password="true" />

        <com.xzy.keyboard.KeyboardEditText
            android:id="@+id/et_input_5"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_below="@+id/et_input_4"
            android:layout_alignParentStart="true"
            android:hint="数字密码"
            android:textSize="80sp"
            keyboard:inputType="number"
            keyboard:password="true" />

        <Button
            android:id="@+id/button"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_below="@+id/et_input_5"
            android:layout_alignParentStart="true"
            android:layout_marginTop="80dp"
            android:text="Button"
            android:textSize="80sp" />
    </RelativeLayout>
</ScrollView>

```

