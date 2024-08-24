import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONArray
import org.json.JSONObject

class SharedViewModel : ViewModel() {
    private val _jsonData = MutableLiveData<JSONObject>()
    private val _history = MutableLiveData<JSONArray>()
    val jsonData: LiveData<JSONObject> get() = _jsonData
    val history: LiveData<JSONArray> get() = _history

    fun setJsonData(jsonObject: JSONObject) {
        _jsonData.value = jsonObject
    }
    fun setHistory(jsonArray: JSONArray) {
        _history.value = jsonArray
        Log.d("loggerboi",jsonArray.toString()+"inside shared")
    }
}