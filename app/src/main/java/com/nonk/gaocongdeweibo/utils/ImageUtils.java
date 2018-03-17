package com.nonk.gaocongdeweibo.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.nonk.gaocongdeweibo.BaseActivity;
import com.nonk.gaocongdeweibo.R;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;

/**
 * Created by monk on 2018/3/15.
 */

public class ImageUtils {
    public static final int REQUEST_CODE_FROM_CAMERA = 5001;
    public static final int REQUEST_CODE_FROM_ALBUM = 5002;


    //存储拍照图片的URI地址
    public static Uri imageUriFromCamera;

    public static void showImagePickDialog(final BaseActivity activity) {
        String[] items = new String[]{"拍照", "相册"};
        new LovelyChoiceDialog(activity)
                .setTopColorRes(R.color.geek_green)
                .setTitle("选择照片来源")
                .setMessage("选择图片来源")
                .setItems(items, new LovelyChoiceDialog.OnItemSelectedListener<String>() {

                    @Override
                    public void onItemSelected(int position, String item) {
                        if (position == 0) {
                            checkPermissions(activity);
                        }else if(position==1){
                            pickImageFromAlbum(activity);
                        }
                    }
                })
                .show();
    }

    /**
     * 检查权限，有权限了再进行后续的操作
     * @param activity
     *        传入的Activity
     */
    private static void checkPermissions(Activity activity){
        if(checkPermission(null,activity,Manifest.permission.WRITE_EXTERNAL_STORAGE, "获取拍照权限",0)) {
            pickImageFromCamera(activity);
        }else {
            ToastUtils.showToast(activity,"没有权限",Toast.LENGTH_SHORT);
            return;
        }
    }

    /**
     * 用相机拍照来选取照片
     * @param activity
     */
    public static void pickImageFromCamera(final Activity activity){

        imageUriFromCamera=createImageUri(activity);
        checkPermission(null,activity,Manifest.permission.CAMERA, "获取拍照权限",0);
        Intent intent=new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUriFromCamera);
        activity.startActivityForResult(intent,REQUEST_CODE_FROM_CAMERA);
    }
    /**
     * 从fragment检查权限，如果传入的fragment是null，则后面请求权限时会调用ActivityCompat
     * 的方法
     *
     * 检查6.0及以上版本时，应用是否拥有某个权限，拥有则返回true，未拥有则再判断上次
     * 用户是否拒绝过该权限的申请（拒绝过则shouldShowRequestPermissionRationale返回
     * true——这里有些手机如红米(红米 pro)永远返回 false
     * 这里的处理是弹一个对话框引导用户去应用的设置界面打开权限，返回false时这里执行
     * requestPermissions方法，此方法会显示系统默认的一个权限授权提示对话框，并在
     * Activity或Fragment的onRequestPermissionsResult得到回调，注意方法中的requestCode
     * 要与此处相同）
     *
     * @param fragment：如果fragment不为null则调用fragment的方法申请权限（因为有些手机
     * 上在Fragment调用ActivityCompat的 方法申请权限得不到回调，例如红米手机）
     * @param activity：用于弹出提示窗和获取权限
     * @param permission：对应的权限名称，如：Manifest.permission.CAMERA
     * @param hint：引导用户进入设置界面对话框的提示文字
     * @param requestCode：请求码，对应Activity或Fragment的onRequestPermissionsResult
     * 方法的requestCode
     * @return：true-拥有对应的权限 false：未拥有对应的权限
     */
    public static boolean checkPermission(Fragment fragment, final Activity activity, String permission,
                                          String hint, int requestCode) {
        //检查权限
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                //显示我们自定义的一个窗口引导用户开启权限
                //showPermissionSettingDialog(activity, hint);

            } else {
                //申请权限
                if (fragment == null) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{permission},
                            requestCode);
                } else {
                    fragment.requestPermissions(
                            new String[]{permission},
                            requestCode);
                }
            }
            return false;
        } else {  //已经拥有权限
            return true;
        }
    }

    /**
     * 创建用于保存拍照后照片的URI
     * @param context
     * @return
     */
    public static Uri createImageUri(Context context){
        String name="gccWeiboImg"+System.currentTimeMillis();
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,name);
        values.put(MediaStore.Images.Media.DISPLAY_NAME,name+".jpeg");
        values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
        Uri uri=context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        return uri;
    }

    public static void pickImageFromAlbum(final Activity activity){
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(intent,REQUEST_CODE_FROM_ALBUM);
    }

    /**
     * 根据Uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= 19) { // api >= 19
            return getRealPathFromUriAboveApi19(context, uri);
        } else { // api < 19
            return getRealPathFromUriBelowAPI19(context, uri);
        }
    }

    /**
     * 适配api19以下(不包括api19),根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    private static String getRealPathFromUriBelowAPI19(Context context, Uri uri) {
        return getDataColumn(context, uri, null, null);
    }

    /**
     * 适配api19及以上,根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    @SuppressLint("NewApi")
    private static String getRealPathFromUriAboveApi19(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) { // MediaProvider
                // 使用':'分割
                String id = documentId.split(":")[1];

                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())){
            // 如果是 content 类型的 Uri
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
            filePath = uri.getPath();
        }
        return filePath;
    }

    /**
     * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
     * @return
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
}
