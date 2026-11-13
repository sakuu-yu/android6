package cn.edu.sicnu.cs.why.ex6.data

import android.content.Context
import android.util.Log
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * 对象序列化存储工具。
 *
 * 写文件使用 [Context.openFileOutput] + [ObjectOutputStream]，
 * 读文件使用 [Context.openFileInput] + [ObjectInputStream]。
 * 被保存的对象必须实现 [Serializable]。
 */
object SerializableStore {

    private const val TAG = "SerializableStore"

    fun save(context: Context, fileName: String, value: Serializable) {
        try {
            ObjectOutputStream(
                context.openFileOutput(fileName, Context.MODE_PRIVATE)
            ).use { output ->
                output.writeObject(value)
            }
        } catch (e: Exception) {
            Log.e(TAG, "保存 $fileName 失败", e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Serializable> load(context: Context, fileName: String): T? {
        // 第一次运行时文件还不存在，直接返回 null 即可。
        if (!context.getFileStreamPath(fileName).exists()) return null

        return try {
            ObjectInputStream(context.openFileInput(fileName)).use { input ->
                input.readObject() as? T
            }
        } catch (e: Exception) {
            // 文件损坏或类结构变化时返回 null，由调用方使用默认数据。
            Log.w(TAG, "读取 $fileName 失败", e)
            null
        }
    }
}
