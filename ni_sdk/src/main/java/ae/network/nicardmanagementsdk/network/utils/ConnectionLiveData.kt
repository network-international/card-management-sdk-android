package ae.network.nicardmanagementsdk.network.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

data class ConnectionModel(
    val type: ConnectionType,
    val isConnected: Boolean,
    val wasConnected: Boolean
)

data class NetworkStateModel(
    val type: ConnectionType,
    val isConnected: Boolean
)

enum class ConnectionType {
    TRANSPORT_VPN,
    TRANSPORT_WIFI,
    TRANSPORT_CELLULAR,
    TRANSPORT_OTHER,
    NO_DATA
}

interface IConnection<T> {
    val hasInternetConnectivity: Boolean
    @MainThread
    fun observe(owner: LifecycleOwner, observer: Observer<in T?>)
}

@RequiresApi(Build.VERSION_CODES.M)
class ConnectionLiveData(context: Context) : MutableLiveData<ConnectionModel>(),
    IConnection<ConnectionModel> {

    @RequiresApi(Build.VERSION_CODES.M)
    private val connectivityManager: ConnectivityManager = context.getSystemService(ConnectivityManager::class.java)
    private val validNetworks: MutableList<Network> = mutableListOf()
    private var wasConnectedBefore: Boolean = true
    private var isFirstTimeRunning: Boolean = true

    override val hasInternetConnectivity: Boolean
        get() = validNetworks.isNotEmpty()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            validNetworks.add(network)
            Log.d("ConnectionLiveData", "onAvailable() called, $network")
            evaluateValidNetworks()
        }

        override fun onLost(network: Network) {
            validNetworks.remove(network)
            Log.d("ConnectionLiveData", "onLost() called, $network")
            evaluateValidNetworks()
        }
    }

    init {
        Log.d("ConnectionLiveData", "init block called")
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)
            .build()
        Log.d("ConnectionLiveData", "registerNetworkCallback")
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onActive() {
        super.onActive()
        if (isFirstTimeRunning) {
            if (validNetworks.isEmpty()) {
                Log.d("ConnectionLiveData", "onActive() generating connection state disconnected")
                generateConnectionState(ConnectionType.NO_DATA, false)
            }
            isFirstTimeRunning = false
        }
    }

    private fun evaluateValidNetworks() {
        getCurrentNetworkStateModel(validNetworks.toList()).also {
            generateConnectionState(it.type, it.isConnected)
        }
    }

    private fun getCurrentNetworkStateModel(networks: List<Network>): NetworkStateModel {
        return networks
            .map { network -> evaluateNetworkType(network) }
            .minByOrNull { it.type } ?: NetworkStateModel(ConnectionType.NO_DATA, false)
    }

    private fun evaluateNetworkType(network: Network): NetworkStateModel {
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        val type = getConnectionType(networkCapabilities)
        return NetworkStateModel(type, true)
    }

    private fun getConnectionType(networkCapabilities: NetworkCapabilities?): ConnectionType {
        return when {
            networkCapabilities == null -> ConnectionType.NO_DATA
            hasTransport(networkCapabilities, NetworkCapabilities.TRANSPORT_VPN) -> ConnectionType.TRANSPORT_VPN
            hasTransport(networkCapabilities, NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionType.TRANSPORT_WIFI
            hasTransport(networkCapabilities, NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectionType.TRANSPORT_CELLULAR
            else -> ConnectionType.TRANSPORT_OTHER
        }
    }

    private fun hasTransport(networkCapabilities: NetworkCapabilities?, transportType: Int) =
        networkCapabilities?.hasTransport(transportType) ?: false

    private fun generateConnectionState(networkType: ConnectionType, isConnected: Boolean) {
        handleNetworkInfo(networkType, isConnected)
        updatePreviousStatus(isConnected)
    }

    private fun handleNetworkInfo(networkType: ConnectionType, isConnected: Boolean) {
        val model = ConnectionModel(
            type = networkType,
            isConnected = isConnected,
            wasConnected = wasConnectedBefore
        )
        postValue(model)
        Log.d("ConnectionLiveData", "we just posted the model = $model")
    }

    private fun updatePreviousStatus(isConnected: Boolean) {
        wasConnectedBefore = isConnected
        Log.d(
            "ConnectionLiveData",
            "wasConnectedBefore status new value = $wasConnectedBefore"
        )
    }
}