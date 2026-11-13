package cn.edu.sicnu.cs.why.ex6.data

import java.io.Serializable

/**
 * 个人信息。
 *
 * 实现 [Serializable] 后，整个对象通过 ObjectOutputStream 写入
 * `profile.dat`，下次启动应用时再通过 ObjectInputStream 恢复。
 */
data class Profile(
    val name: String = "",
    val studentId: String = "",
    val gender: String = "",
    val phone: String = "",
    val email: String = "",
    val hobbies: List<String> = emptyList(),
    /** 最近一次保存的时间，用于在界面上展示保存历史。 */
    val updatedAt: Long = 0L
) : Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }
}
