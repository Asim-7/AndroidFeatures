package com.asim.androidfeatures

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore

object PathUtil {

    fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        var filePath: String? = null
        if (DocumentsContract.isDocumentUri(context, contentUri)) {
            val documentId = DocumentsContract.getDocumentId(contentUri)
            if (isExternalStorageDocument(contentUri)) {
                val split = documentId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    filePath = Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(contentUri)) {
                val uri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), documentId.toLong()
                )
                filePath = getDataColumn(context, uri, null, null)
            } else if (isMediaDocument(contentUri)) {
                val split = documentId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                var uri: Uri? = null
                if ("image" == type) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                filePath = getDataColumn(context, uri, selection, selectionArgs)
            }
        } else if ("content".equals(contentUri.scheme, ignoreCase = true)) {
            filePath = getDataColumn(context, contentUri, null, null)
        } else if ("file".equals(contentUri.scheme, ignoreCase = true)) {
            filePath = contentUri.path
        }
        return filePath
    }

    private fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var filePath: String? = null
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val new = cursor.getString(columnIndex)
            filePath = new
            cursor.close()
        }
        return filePath
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

}