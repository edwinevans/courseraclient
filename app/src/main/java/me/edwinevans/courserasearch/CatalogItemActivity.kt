// See MainActivity for notes

package me.edwinevans.courserasearch

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import com.loopj.android.http.JsonHttpResponseHandler
import com.squareup.picasso.Picasso
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

/**
 * Display details of a Course or Specialization
 */
class CatalogItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog_item)

        val bundle = intent.getBundleExtra(CatalogItem.EXTRA_KEY)
        val catalogItem : CatalogItem = CatalogItem.fromBundle(bundle)
        setTextValue(R.id.name, catalogItem.name)
        setTextValue(R.id.university_name, catalogItem.universityName)
        setTextValue(R.id.number_of_courses, catalogItem.getNumCoursesDisplayString(application))

        val handler = object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject?) {
                val element = response?.optJSONArray("elements")?.get(0) as JSONObject?
                val description = element?.optString("description")
                setTextValue(R.id.description, description)
                val logo : String?
                if (catalogItem.isSpecialization) {
                    logo = element?.optString("logo")
                }
                else {
                    logo = element?.optString("photoUrl")
                }
                if (!TextUtils.isEmpty(logo)) {
                    val imageView = findViewById(R.id.logo) as ImageView
                    Picasso.with(application).load(logo).into(imageView);
                }
            }
        }
        
        if (catalogItem.isSpecialization) {
            CourseraApiClient.getSpecialization(application, catalogItem.id, handler)
        }
        else {
            CourseraApiClient.getCourse(application, catalogItem.id, handler)
        }
    }

    private fun setTextValue(resourceId: Int, value: String?) {
        val tvName : TextView = findViewById(resourceId) as TextView
        tvName.text = value
    }
}
