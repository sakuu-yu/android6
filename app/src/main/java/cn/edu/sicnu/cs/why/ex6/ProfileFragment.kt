package cn.edu.sicnu.cs.why.ex6

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import cn.edu.sicnu.cs.why.ex6.data.AvatarStore
import cn.edu.sicnu.cs.why.ex6.data.GameStore
import cn.edu.sicnu.cs.why.ex6.data.Profile
import cn.edu.sicnu.cs.why.ex6.data.ProfileStore
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 第 1 个 Fragment：个人信息。
 *
 * 展示与编辑合并在一个 Fragment 中：默认展示已保存的信息，点击
 * 「修改个人信息」后切换到编辑状态。保存时把 [Profile] 对象通过
 * 对象序列化写入 `profile.dat`，重新打开应用后自动恢复。
 */
class ProfileFragment : Fragment() {

    private lateinit var pendingAvatarFile: File
    private var pendingAvatarSelected = false
    private var editing = false

    private lateinit var groupProfileView: View
    private lateinit var groupProfileEdit: View
    private lateinit var btnPickAvatar: ImageButton
    private lateinit var ivAvatar: ImageView
    private lateinit var avatarPlaceholder: View

    private val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri == null) return@registerForActivityResult
            val context = context ?: return@registerForActivityResult
            if (AvatarStore.copyFromUri(context, uri, pendingAvatarFile)) {
                pendingAvatarSelected = true
                showAvatar(AvatarStore.decodeFile(pendingAvatarFile))
            } else {
                Toast.makeText(context, R.string.toast_avatar_error, Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pendingAvatarFile = File(requireContext().cacheDir, "pending_avatar.png")

        groupProfileView = view.findViewById(R.id.groupProfileView)
        groupProfileEdit = view.findViewById(R.id.groupProfileEdit)
        btnPickAvatar = view.findViewById(R.id.btnPickAvatar)
        ivAvatar = view.findViewById(R.id.ivAvatar)
        avatarPlaceholder = view.findViewById(R.id.avatarPlaceholder)

        btnPickAvatar.setOnClickListener { pickImage.launch("image/*") }
        view.findViewById<View>(R.id.btnEditProfile).setOnClickListener { enterEditMode() }
        view.findViewById<View>(R.id.btnSave).setOnClickListener { saveProfile() }
        view.findViewById<View>(R.id.btnCancel).setOnClickListener { cancelEdit() }

        renderProfile(ProfileStore.load(requireContext()))
    }

    override fun onResume() {
        super.onResume()
        if (!editing) {
            renderProfile(ProfileStore.load(requireContext()))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::pendingAvatarFile.isInitialized && pendingAvatarFile.exists()) {
            pendingAvatarFile.delete()
        }
    }

    private fun renderProfile(profile: Profile) {
        if (view == null) return
        val context = requireContext()

        setValue(R.id.tvProfileName, profile.name)
        setValue(R.id.tvProfileGender, profile.gender)
        setValue(R.id.tvProfileStudentId, profile.studentId)
        setValue(R.id.tvProfilePhone, profile.phone)
        setValue(R.id.tvProfileEmail, profile.email)

        view?.findViewById<TextView>(R.id.tvProfileHobbies)?.text =
            if (profile.hobbies.isEmpty()) {
                getString(R.string.value_not_selected)
            } else {
                profile.hobbies.joinToString(getString(R.string.hobby_separator))
            }

        view?.findViewById<TextView>(R.id.tvProfileBestTwo)?.text =
            getString(R.string.best_score_format, GameStore.bestScore(context, 2))
        view?.findViewById<TextView>(R.id.tvProfileBestThree)?.text =
            getString(R.string.best_score_format, GameStore.bestScore(context, 3))

        view?.findViewById<TextView>(R.id.tvProfileUpdated)?.text =
            if (profile.updatedAt > 0) {
                timeFormat.format(Date(profile.updatedAt))
            } else {
                getString(R.string.value_not_saved)
            }

        showAvatar(AvatarStore.load(context))
    }

    private fun setValue(viewId: Int, value: String) {
        view?.findViewById<TextView>(viewId)?.text =
            value.ifBlank { getString(R.string.value_not_filled) }
    }

    private fun enterEditMode() {
        editing = true
        pendingAvatarSelected = false
        if (pendingAvatarFile.exists()) pendingAvatarFile.delete()

        loadProfileIntoForm(ProfileStore.load(requireContext()))
        groupProfileView.visibility = View.GONE
        groupProfileEdit.visibility = View.VISIBLE
        btnPickAvatar.visibility = View.VISIBLE
        showAvatar(AvatarStore.load(requireContext()))
    }

    private fun cancelEdit() {
        editing = false
        pendingAvatarSelected = false
        if (pendingAvatarFile.exists()) pendingAvatarFile.delete()

        groupProfileEdit.visibility = View.GONE
        groupProfileView.visibility = View.VISIBLE
        btnPickAvatar.visibility = View.GONE
        renderProfile(ProfileStore.load(requireContext()))
    }

    private fun loadProfileIntoForm(profile: Profile) {
        view?.findViewById<EditText>(R.id.etName)?.setText(profile.name)
        view?.findViewById<EditText>(R.id.etStudentId)?.setText(profile.studentId)
        view?.findViewById<EditText>(R.id.etPhone)?.setText(profile.phone)
        view?.findViewById<EditText>(R.id.etEmail)?.setText(profile.email)

        val female = profile.gender == getString(R.string.gender_female)
        view?.findViewById<RadioButton>(R.id.rbFemale)?.isChecked = female
        view?.findViewById<RadioButton>(R.id.rbMale)?.isChecked = !female

        view?.findViewById<CheckBox>(R.id.cbTravel)?.isChecked =
            profile.hobbies.contains(getString(R.string.hobby_travel))
        view?.findViewById<CheckBox>(R.id.cbReading)?.isChecked =
            profile.hobbies.contains(getString(R.string.hobby_reading))
        view?.findViewById<CheckBox>(R.id.cbGame)?.isChecked =
            profile.hobbies.contains(getString(R.string.hobby_game))
        view?.findViewById<CheckBox>(R.id.cbShopping)?.isChecked =
            profile.hobbies.contains(getString(R.string.hobby_shopping))
    }

    private fun saveProfile() {
        val currentView = view ?: return
        val context = requireContext()

        val hobbies = mutableListOf<String>()
        if (currentView.findViewById<CheckBox>(R.id.cbTravel).isChecked) {
            hobbies.add(getString(R.string.hobby_travel))
        }
        if (currentView.findViewById<CheckBox>(R.id.cbReading).isChecked) {
            hobbies.add(getString(R.string.hobby_reading))
        }
        if (currentView.findViewById<CheckBox>(R.id.cbGame).isChecked) {
            hobbies.add(getString(R.string.hobby_game))
        }
        if (currentView.findViewById<CheckBox>(R.id.cbShopping).isChecked) {
            hobbies.add(getString(R.string.hobby_shopping))
        }

        val gender = if (currentView.findViewById<RadioButton>(R.id.rbFemale).isChecked) {
            getString(R.string.gender_female)
        } else {
            getString(R.string.gender_male)
        }

        val profile = Profile(
            name = currentView.findViewById<EditText>(R.id.etName).text.toString().trim(),
            studentId = currentView.findViewById<EditText>(R.id.etStudentId).text.toString().trim(),
            gender = gender,
            phone = currentView.findViewById<EditText>(R.id.etPhone).text.toString().trim(),
            email = currentView.findViewById<EditText>(R.id.etEmail).text.toString().trim(),
            hobbies = hobbies
        )
        val saved = ProfileStore.save(context, profile)

        if (pendingAvatarSelected && pendingAvatarFile.exists()) {
            try {
                pendingAvatarFile.copyTo(AvatarStore.avatarFile(context), overwrite = true)
                pendingAvatarFile.delete()
            } catch (e: Exception) {
                Toast.makeText(context, R.string.toast_avatar_error, Toast.LENGTH_SHORT).show()
            }
        }

        pendingAvatarSelected = false
        editing = false
        groupProfileEdit.visibility = View.GONE
        groupProfileView.visibility = View.VISIBLE
        btnPickAvatar.visibility = View.GONE
        renderProfile(saved)
        Toast.makeText(context, R.string.toast_save_success, Toast.LENGTH_SHORT).show()
    }

    private fun showAvatar(bitmap: Bitmap?) {
        if (view == null) return
        if (bitmap != null) {
            ivAvatar.setImageBitmap(bitmap)
            ivAvatar.visibility = View.VISIBLE
            avatarPlaceholder.visibility = View.GONE
        } else {
            ivAvatar.setImageDrawable(null)
            ivAvatar.visibility = View.GONE
            avatarPlaceholder.visibility = View.VISIBLE
        }
    }
}
