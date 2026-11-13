package cn.edu.sicnu.cs.why.ex6.data

import android.content.Context

/** 个人信息的序列化读写。 */
object ProfileStore {

    const val FILE_NAME = "profile.dat"

    fun load(context: Context): Profile =
        SerializableStore.load<Profile>(context, FILE_NAME) ?: Profile()

    /** 保存成功后会补上当前时间戳，并返回真正写入文件的对象。 */
    fun save(context: Context, profile: Profile): Profile {
        val saved = profile.copy(updatedAt = System.currentTimeMillis())
        SerializableStore.save(context, FILE_NAME, saved)
        return saved
    }
}
