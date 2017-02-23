package me.edwinevans.courserasearch

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class CatalogItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog_item)

        val bundle = intent.getBundleExtra(CatalogItem.EXTRA_KEY)
        val catalogItem : CatalogItem = CatalogItem.fromBundle(bundle)

        setTextValue(R.id.name, catalogItem.name)
        setTextValue(R.id.university_name, catalogItem.universityName)
        setTextValue(R.id.number_of_courses, catalogItem.getNumCoursesDisplayString(application))
    }

    private fun setTextValue(resourceId: Int, value: String?) {
        val tvName : TextView = findViewById(resourceId) as TextView
        tvName.setText(value)
    }
}
