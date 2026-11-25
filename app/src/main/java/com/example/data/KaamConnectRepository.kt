package com.example.data

import android.util.Log
import com.example.model.AcceptedWorker
import com.example.model.ContractorProfile
import com.example.model.KaamConstants
import com.example.model.LiveRequest
import com.example.model.WorkerProfile
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class KaamConnectRepository(private val db: FirebaseFirestore) {

  private val workersRef = db.collection("workers")
  private val contractorsRef = db.collection("contractors")
  private val liveRequestsRef = db.collection("live_requests")

  fun observeWorkers(): Flow<List<WorkerProfile>> = callbackFlow {
    val registration =
      workersRef
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .addSnapshotListener { snapshot, error ->
          if (error != null) {
            Log.e("KaamRepo", "observeWorkers error", error)
            // Fallback unordered query if index/field differs on existing docs
            workersRef.get().addOnSuccessListener { fallbackSnap ->
              val items =
                fallbackSnap.documents
                  .mapNotNull { parseWorker(it) }
                  .sortedByDescending { it.createdAtMs }
              trySend(items)
            }.addOnFailureListener {
              close(error)
            }
            return@addSnapshotListener
          }
          val items =
            snapshot?.documents
              ?.mapNotNull { parseWorker(it) }
              ?.sortedByDescending { it.createdAtMs }
              ?: emptyList()
          trySend(items)
        }
    awaitClose { registration.remove() }
  }

  fun observeContractors(): Flow<List<ContractorProfile>> = callbackFlow {
    val registration =
      contractorsRef
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .addSnapshotListener { snapshot, error ->
          if (error != null) {
            Log.e("KaamRepo", "observeContractors error", error)
            contractorsRef.get().addOnSuccessListener { fallbackSnap ->
              val items =
                fallbackSnap.documents
                  .mapNotNull { parseContractor(it) }
                  .sortedByDescending { it.createdAtMs }
              trySend(items)
            }.addOnFailureListener {
              close(error)
            }
            return@addSnapshotListener
          }
          val items =
            snapshot?.documents
              ?.mapNotNull { parseContractor(it) }
              ?.sortedByDescending { it.createdAtMs }
              ?: emptyList()
          trySend(items)
        }
    awaitClose { registration.remove() }
  }

  fun observeLiveRequests(): Flow<List<LiveRequest>> = callbackFlow {
    val registration =
      liveRequestsRef
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .addSnapshotListener { snapshot, error ->
          if (error != null) {
            Log.e("KaamRepo", "observeLiveRequests error", error)
            liveRequestsRef.get().addOnSuccessListener { fallbackSnap ->
              val items =
                fallbackSnap.documents
                  .mapNotNull { parseLiveRequest(it) }
                  .sortedByDescending { it.createdAtMs }
              trySend(items)
            }.addOnFailureListener {
              close(error)
            }
            return@addSnapshotListener
          }
          val items =
            snapshot?.documents
              ?.mapNotNull { parseLiveRequest(it) }
              ?.sortedByDescending { it.createdAtMs }
              ?: emptyList()
          trySend(items)
        }
    awaitClose { registration.remove() }
  }

  suspend fun saveWorkerProfile(profile: WorkerProfile): Result<Unit> = runCatchingSafe {
    val cleanPhone = profile.phone.filter { it.isDigit() }.takeLast(10)
    require(profile.name.isNotBlank()) { "Full name is required." }
    require(cleanPhone.length == 10) { "Enter a valid 10-digit mobile number." }
    require(profile.skill.isNotBlank()) { "Please select or enter a skill/trade." }

    val existing = workersRef.whereEqualTo("phone", cleanPhone).limit(1).get().await()
    val docRef =
      if (profile.id.isNotBlank()) {
        workersRef.document(profile.id)
      } else if (!existing.isEmpty) {
        existing.documents.first().reference
      } else {
        workersRef.document(cleanPhone)
      }

    val payload =
      mutableMapOf<String, Any>(
        "name" to profile.name.trim(),
        "phone" to cleanPhone,
        "skill" to profile.skill.trim(),
        "experienceYears" to profile.experienceYears.coerceAtLeast(0),
        "dailyRate" to profile.dailyRate.coerceAtLeast(0),
        "registrationType" to profile.registrationType,
        "teamSize" to if (profile.isTeamLeader) profile.teamSize.coerceAtLeast(2) else 1,
        "state" to profile.state.trim().ifBlank { KaamConstants.DEFAULT_STATE },
        "city" to profile.city.trim().ifBlank { KaamConstants.DEFAULT_CITY },
        "outstationReady" to profile.outstationReady,
        "isAvailable" to profile.isAvailable,
        "isOnline" to profile.isOnline,
        "updatedAt" to FieldValue.serverTimestamp(),
      )
    if (existing.isEmpty && profile.id.isBlank()) {
      payload["createdAt"] = FieldValue.serverTimestamp()
    }
    docRef.set(payload, SetOptions.merge()).await()
  }

  suspend fun toggleWorkerAvailability(workerId: String, isAvailable: Boolean): Result<Unit> =
    runCatchingSafe {
      require(workerId.isNotBlank()) { "Worker profile not found." }
      workersRef
        .document(workerId)
        .update(
          mapOf(
            "isAvailable" to isAvailable,
            "updatedAt" to FieldValue.serverTimestamp(),
          )
        )
        .await()
    }

  suspend fun toggleWorkerOnline(workerId: String, isOnline: Boolean): Result<Unit> =
    runCatchingSafe {
      require(workerId.isNotBlank()) { "Worker profile not found." }
      workersRef
        .document(workerId)
        .update(
          mapOf(
            "isOnline" to isOnline,
            "updatedAt" to FieldValue.serverTimestamp(),
          )
        )
        .await()
    }

  suspend fun saveContractorProfile(profile: ContractorProfile): Result<Unit> = runCatchingSafe {
    val cleanPhone = profile.phone.filter { it.isDigit() }.takeLast(10)
    require(profile.name.isNotBlank()) { "Contractor name is required." }
    require(cleanPhone.length == 10) { "Enter a valid 10-digit mobile number." }
    require(profile.tradesNeeded.isNotEmpty()) { "Select at least one trade you hire." }

    val existing = contractorsRef.whereEqualTo("phone", cleanPhone).limit(1).get().await()
    val docRef =
      if (profile.id.isNotBlank()) {
        contractorsRef.document(profile.id)
      } else if (!existing.isEmpty) {
        existing.documents.first().reference
      } else {
        contractorsRef.document(cleanPhone)
      }

    val payload =
      mutableMapOf<String, Any>(
        "name" to profile.name.trim(),
        "companyName" to profile.companyName.trim(),
        "phone" to cleanPhone,
        "tradesNeeded" to profile.tradesNeeded.map { it.trim() }.filter { it.isNotBlank() },
        "state" to profile.state.trim().ifBlank { KaamConstants.DEFAULT_STATE },
        "city" to profile.city.trim().ifBlank { KaamConstants.DEFAULT_CITY },
        "facilities" to profile.facilities,
        "isHiring" to profile.isHiring,
        "updatedAt" to FieldValue.serverTimestamp(),
      )
    if (existing.isEmpty && profile.id.isBlank()) {
      payload["createdAt"] = FieldValue.serverTimestamp()
    }
    docRef.set(payload, SetOptions.merge()).await()
  }

  suspend fun toggleContractorHiring(contractorId: String, isHiring: Boolean): Result<Unit> =
    runCatchingSafe {
      require(contractorId.isNotBlank()) { "Contractor profile not found." }
      contractorsRef
        .document(contractorId)
        .update(
          mapOf(
            "isHiring" to isHiring,
            "updatedAt" to FieldValue.serverTimestamp(),
          )
        )
        .await()
    }

  suspend fun createLiveRequest(request: LiveRequest): Result<Unit> = runCatchingSafe {
    val cleanPhone = request.contractorPhone.filter { it.isDigit() }.takeLast(10)
    require(request.contractorName.isNotBlank()) { "Contractor name is required." }
    require(cleanPhone.length == 10) { "Enter a valid 10-digit mobile number." }
    require(request.skill.isNotBlank()) { "Skill needed is required." }
    require(request.requiredCount > 0) { "Workers needed must be at least 1." }
    require(request.dailyWage > 0) { "Enter a valid daily wage." }

    val payload =
      mapOf(
        "contractorName" to request.contractorName.trim(),
        "contractorPhone" to cleanPhone,
        "skill" to request.skill.trim(),
        "requiredCount" to request.requiredCount,
        "confirmedCount" to 0,
        "dailyWage" to request.dailyWage,
        "state" to request.state.trim().ifBlank { KaamConstants.DEFAULT_STATE },
        "city" to request.city.trim().ifBlank { KaamConstants.DEFAULT_CITY },
        "siteAddress" to request.siteAddress.trim(),
        "facilities" to request.facilities,
        "status" to "active",
        "acceptedWorkers" to emptyList<Map<String, Any>>(),
        "reportedByPhones" to emptyList<String>(),
        "createdAt" to FieldValue.serverTimestamp(),
      )
    liveRequestsRef.add(payload).await()
  }

  suspend fun acceptLiveRequest(
    requestId: String,
    workerName: String,
    workerPhone: String,
    workerSkill: String,
  ): Result<Unit> = runCatchingSafe {
    val cleanPhone = workerPhone.filter { it.isDigit() }.takeLast(10)
    require(requestId.isNotBlank()) { "Invalid request ID." }
    require(workerName.isNotBlank()) { "Please enter your name first." }
    require(cleanPhone.length == 10) { "Please login with a 10-digit mobile number first." }
    val resolvedSkill = workerSkill.trim().ifBlank { "Helper / Labour" }

    val docRef = liveRequestsRef.document(requestId)
    db.runTransaction { transaction ->
        val snapshot = transaction.get(docRef)
        if (!snapshot.exists()) {
          throw IllegalStateException("This live request is no longer available.")
        }
        val status = snapshot.getString("status").orEmpty()
        if (status.equals("closed", ignoreCase = true)) {
          throw IllegalStateException("This request has been closed by the contractor.")
        }
        val contractorPhone = snapshot.getString("contractorPhone").orEmpty()
        if (contractorPhone == cleanPhone) {
          throw IllegalStateException("This is your own request.")
        }

        val rawAccepted = snapshot.get("acceptedWorkers") as? List<*> ?: emptyList<Any>()
        val existingList =
          rawAccepted.mapNotNull { item ->
            val map = item as? Map<*, *> ?: return@mapNotNull null
            mapOf(
              "name" to (map["name"] as? String).orEmpty(),
              "phone" to (map["phone"] as? String).orEmpty(),
              "skill" to (map["skill"] as? String).orEmpty(),
              "acceptedAtMs" to ((map["acceptedAtMs"] as? Number)?.toLong() ?: 0L),
            )
          }

        if (existingList.any { it["phone"] == cleanPhone }) {
          throw IllegalStateException("You have already accepted this job.")
        }

        val requiredCount = (snapshot.get("requiredCount") as? Number)?.toInt() ?: 1
        val confirmedCount =
          (snapshot.get("confirmedCount") as? Number)?.toInt() ?: existingList.size

        if (confirmedCount >= requiredCount) {
          throw IllegalStateException("All Slots Filled")
        }

        val updatedList =
          existingList +
            mapOf(
              "name" to workerName.trim(),
              "phone" to cleanPhone,
              "skill" to resolvedSkill,
              "acceptedAtMs" to System.currentTimeMillis(),
            )

        transaction.update(
          docRef,
          mapOf(
            "confirmedCount" to (confirmedCount + 1),
            "acceptedWorkers" to updatedList,
          ),
        )
      }
      .await()
  }

  suspend fun closeLiveRequest(requestId: String): Result<Unit> = runCatchingSafe {
    require(requestId.isNotBlank()) { "Invalid request ID." }
    liveRequestsRef
      .document(requestId)
      .update(mapOf("status" to "closed"))
      .await()
  }

  suspend fun reportLiveRequest(requestId: String, reporterPhone: String): Result<Unit> =
    runCatchingSafe {
      require(requestId.isNotBlank()) { "Invalid request ID." }
      val identifier = reporterPhone.ifBlank { "anonymous" }
      liveRequestsRef
        .document(requestId)
        .update("reportedByPhones", FieldValue.arrayUnion(identifier))
        .await()
    }

  private fun parseWorker(doc: DocumentSnapshot): WorkerProfile? {
    val name = doc.getString("name").orEmpty().trim()
    val phone = doc.getString("phone").orEmpty().trim()
    if (name.isBlank() && phone.isBlank()) return null

    val teamSize = (doc.get("teamSize") as? Number)?.toInt() ?: 1
    val registrationType =
      doc.getString("registrationType")?.takeIf { it.isNotBlank() }
        ?: if (teamSize > 1) "Team Leader (With Squad)" else "Individual Worker"

    return WorkerProfile(
      id = doc.id,
      name = name,
      phone = phone,
      skill = doc.getString("skill").orEmpty().ifBlank { "Helper / Labour" },
      experienceYears =
        ((doc.get("experienceYears") ?: doc.get("experience")) as? Number)?.toInt() ?: 0,
      dailyRate = (doc.get("dailyRate") as? Number)?.toInt() ?: 0,
      registrationType = registrationType,
      teamSize = teamSize.coerceAtLeast(1),
      state = doc.getString("state").orEmpty().ifBlank { KaamConstants.DEFAULT_STATE },
      city = doc.getString("city").orEmpty().ifBlank { KaamConstants.DEFAULT_CITY },
      outstationReady =
        (doc.getBoolean("outstationReady") ?: doc.getBoolean("outstation")) ?: false,
      isAvailable = doc.getBoolean("isAvailable") ?: true,
      isOnline = doc.getBoolean("isOnline") ?: false,
      createdAtMs = extractTimestampMillis(doc, "createdAt"),
    )
  }

  private fun parseContractor(doc: DocumentSnapshot): ContractorProfile? {
    val name = doc.getString("name").orEmpty().trim()
    val phone = doc.getString("phone").orEmpty().trim()
    if (name.isBlank() && phone.isBlank()) return null

    val rawTrades = (doc.get("tradesNeeded") ?: doc.get("trades")) as? List<*> ?: emptyList<Any>()
    val trades = rawTrades.mapNotNull { it as? String }.filter { it.isNotBlank() }
    val rawFacilities = doc.get("facilities") as? List<*> ?: emptyList<Any>()
    val facilities = rawFacilities.mapNotNull { it as? String }.filter { it.isNotBlank() }

    return ContractorProfile(
      id = doc.id,
      name = name,
      companyName = doc.getString("companyName").orEmpty(),
      phone = phone,
      tradesNeeded = trades,
      state = doc.getString("state").orEmpty().ifBlank { KaamConstants.DEFAULT_STATE },
      city = doc.getString("city").orEmpty().ifBlank { KaamConstants.DEFAULT_CITY },
      facilities = facilities,
      isHiring = doc.getBoolean("isHiring") ?: true,
      createdAtMs = extractTimestampMillis(doc, "createdAt"),
    )
  }

  private fun parseLiveRequest(doc: DocumentSnapshot): LiveRequest? {
    val contractorName = doc.getString("contractorName").orEmpty().trim()
    val contractorPhone = doc.getString("contractorPhone").orEmpty().trim()
    if (contractorName.isBlank() && contractorPhone.isBlank()) return null

    val rawFacilities = doc.get("facilities") as? List<*> ?: emptyList<Any>()
    val facilities = rawFacilities.mapNotNull { it as? String }.filter { it.isNotBlank() }

    val rawAccepted = doc.get("acceptedWorkers") as? List<*> ?: emptyList<Any>()
    val acceptedWorkers =
      rawAccepted.mapNotNull { item ->
        val map = item as? Map<*, *> ?: return@mapNotNull null
        AcceptedWorker(
          name = (map["name"] as? String).orEmpty(),
          phone = (map["phone"] as? String).orEmpty(),
          skill = (map["skill"] as? String).orEmpty(),
          acceptedAtMs = (map["acceptedAtMs"] as? Number)?.toLong() ?: 0L,
        )
      }

    val rawReports = doc.get("reportedByPhones") as? List<*> ?: emptyList<Any>()
    val reports = rawReports.mapNotNull { it as? String }

    return LiveRequest(
      id = doc.id,
      contractorName = contractorName,
      contractorPhone = contractorPhone,
      skill = doc.getString("skill").orEmpty().ifBlank { "Helper / Labour" },
      requiredCount = ((doc.get("requiredCount") as? Number)?.toInt() ?: 1).coerceAtLeast(1),
      confirmedCount = (doc.get("confirmedCount") as? Number)?.toInt() ?: acceptedWorkers.size,
      dailyWage = (doc.get("dailyWage") as? Number)?.toInt() ?: 0,
      state = doc.getString("state").orEmpty().ifBlank { KaamConstants.DEFAULT_STATE },
      city = doc.getString("city").orEmpty().ifBlank { KaamConstants.DEFAULT_CITY },
      siteAddress = doc.getString("siteAddress").orEmpty(),
      facilities = facilities,
      status = doc.getString("status").orEmpty().ifBlank { "active" },
      acceptedWorkers = acceptedWorkers,
      reportedByPhones = reports,
      createdAtMs = extractTimestampMillis(doc, "createdAt"),
    )
  }

  private fun extractTimestampMillis(doc: DocumentSnapshot, field: String): Long {
    val ts = doc.getTimestamp(field, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
    if (ts != null) return ts.toDate().time
    val numeric = (doc.get(field) as? Number)?.toLong()
    return numeric ?: System.currentTimeMillis()
  }

  private inline fun <T> runCatchingSafe(block: () -> T): Result<T> {
    return try {
      Result.success(block())
    } catch (ce: CancellationException) {
      throw ce
    } catch (t: Throwable) {
      Log.e("KaamRepo", "Firestore operation failed", t)
      Result.failure(t)
    }
  }
}
