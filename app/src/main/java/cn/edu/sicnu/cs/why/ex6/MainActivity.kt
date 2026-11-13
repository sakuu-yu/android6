package cn.edu.sicnu.cs.why.ex6

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * 应用主界面：Toolbar + Fragment 容器 + 底部导航。
 *
 * 底部导航负责在三个 Fragment 之间切换：
 * 个人信息、两张牌翻牌游戏、三张牌翻牌游戏。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))
        supportActionBar?.title = getString(R.string.app_name)

        bottomNav = findViewById(R.id.bottomNav)
        val initialItem = savedInstanceState?.getInt(KEY_SELECTED_ITEM) ?: R.id.nav_profile

        // 先恢复选中项，再设置监听器，避免恢复时重复创建 Fragment。
        bottomNav.selectedItemId = initialItem
        if (savedInstanceState == null) {
            showFragment(initialItem)
        }
        bottomNav.setOnItemSelectedListener { item ->
            showFragment(item.itemId)
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED_ITEM, bottomNav.selectedItemId)
    }

    private fun showFragment(itemId: Int) {
        val fragment: Fragment = when (itemId) {
            R.id.nav_two_cards -> TwoCardGameFragment()
            R.id.nav_three_cards -> ThreeCardGameFragment()
            else -> ProfileFragment()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                showAboutDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.about_title)
            .setMessage(R.string.about_message)
            .setPositiveButton(R.string.btn_ok, null)
            .show()
    }

    companion object {
        private const val KEY_SELECTED_ITEM = "selected_item"
    }
}
