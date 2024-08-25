import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONArray
import org.json.JSONObject

class SharedViewModel : ViewModel() {
//    companion object {
//        private var instance: SharedViewModel? = null
//
//        fun getInstance(): SharedViewModel {
//            if (instance == null) {
//                instance = SharedViewModel()
//            }
//            return instance!!
//        }
//    }
    private val _jsonData = MutableLiveData<JSONObject>()
    private val _history = MutableLiveData<JSONArray>()
    private val _prayertimes = MutableLiveData<JSONObject>()
    val jsonData: LiveData<JSONObject> get() = _jsonData
    val history: LiveData<JSONArray> get() = _history
    val prayertimes: LiveData<JSONObject> get() = _prayertimes

    fun setJsonData(jsonObject: JSONObject) {
        _jsonData.value = jsonObject
    }
    fun setHistory(jsonArray: JSONArray) {
        _history.value = jsonArray
    }
    fun setPrayertimes(jsonObject: JSONObject){
        _prayertimes.value = jsonObject
    }
}