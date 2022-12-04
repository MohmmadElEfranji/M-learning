package com.nerds.m_learning.teacher.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.student.model.TextMessage
import com.nerds.m_learning.teacher.model.Teachers
import com.nerds.m_learning.teacher.model.Teachers.Companion.CHILD_PATH_ASSIGNMENT_OF_Lectures
import com.nerds.m_learning.teacher.model.Teachers.Companion.CHILD_PATH_CHAPTERS_OF_Lectures
import com.nerds.m_learning.teacher.model.Teachers.Companion.CHILD_PATH_COURSES
import com.nerds.m_learning.teacher.model.Teachers.Companion.CHILD_PATH_Lectures
import kotlinx.coroutines.tasks.await

object TeachersFirebaseRepo {

    private const val TAG = "_TEST"
    private val storage = Firebase.storage
    private var storageRef = storage.reference
    private val collectionRef = Firebase.firestore.collection(Students.COLLECTION_STUDENTS)

    /*----------------------------------*/
    private val collectionRefTeachers = Firebase.firestore.collection(Teachers.COLLECTION_TEACHERS)
    private val collectionRefChatChannel =
        Firebase.firestore.collection(Teachers.COLLECTION_CHAT_CHANNEL)
    private val collectionRefAllCourses =
        Firebase.firestore.collection(Teachers.COLLECTION_ALL_COURSES)

    /*----------------------------------*/
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    /*----------------------------------*/
    private var courseses: MutableList<Teachers.Courses> = ArrayList()

    private suspend fun signUpOnFirestoreTeacher(
        teacher: Teachers.Teacher
    ): MutableLiveData<FirebaseResponse<Unit>> {
        return try {
            collectionRefTeachers.document(teacher.teacherID).set(teacher).await()
            MutableLiveData(FirebaseResponse.Success(Unit))
        } catch (exception: Exception) {
            MutableLiveData(FirebaseResponse.Failure(message = exception.message))
        }
    }

    suspend fun registerNewTeacherAccount(teacher: Teachers.Teacher): MutableLiveData<FirebaseResponse<Unit>> {
        return try {
            val result =
                auth.createUserWithEmailAndPassword(teacher.email!!, teacher.password!!).await()
            val userID = result.user?.uid
            teacher.teacherID = userID!!
            signUpOnFirestoreTeacher(teacher)
        } catch (exception: Exception) {
            MutableLiveData(FirebaseResponse.Failure(message = exception.message!!))
        }
    }

    /*----------------------------------*//*----------------------------------*/
    /*----------------------------------*//*----------------------------------*/

    //region New SignUp
    private suspend fun signUpOnFirestoreTeacher2(teacher: Teachers.Teacher): FirebaseResponse<Boolean> {
        return try {
            collectionRefTeachers.document(teacher.teacherID).set(teacher).await()
            FirebaseResponse.Success(true)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message!!)
        }

    }

    suspend fun registerNewTeacherAccount2(teacher: Teachers.Teacher): FirebaseResponse<Boolean> {

        return try {
            val result =
                auth.createUserWithEmailAndPassword(teacher.email!!, teacher.password!!).await()
            val userID = result.user!!.uid
            teacher.teacherID = userID

            signUpOnFirestoreTeacher2(teacher)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message!!)
        }
    }

    //endregion

    // region Log in

    private suspend fun checkStudentAccountExistsOnFireStore(teacherID: String): FirebaseResponse<Boolean> {
        return try {
            val result = collectionRefTeachers.document(teacherID).get().await()
            FirebaseResponse.Success(result.exists())
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message!!)
        }
    }

    suspend fun loginOnFirebaseAuth(
        teacherEmail: String,
        teacherPassword: String
    ): FirebaseResponse<Boolean> {
        return try {
            val result =
                auth.signInWithEmailAndPassword(teacherEmail, teacherPassword).await()
            val userID = result.user!!.uid
            checkStudentAccountExistsOnFireStore(userID)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message!!)
        }
    }

    // endregion
    /*----------------------------------*//*----------------------------------*/
    //region Add course
    suspend fun addCourse(course: Teachers.Courses): FirebaseResponse<Boolean> {
        return try {
            collectionRefTeachers.document(course.teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(course.courseID).set(course).await()
            collectionRefAllCourses.document(course.courseID).set(course).await()
            FirebaseResponse.Success(true)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }

    suspend fun addChatChannelForTeacher(
        teacherID: String,
        studentID: String,
        chatChannelID: Teachers.ChatChannelForUser
    ): FirebaseResponse<Boolean> {
        return try {
            collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_CHAT_CHANNEL)
                .document(studentID).set(chatChannelID).await()
            FirebaseResponse.Success(true)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }


    suspend fun getChatChannelForTeacher(
        teacherID: String,
        studentID: String
    ): FirebaseResponse<Teachers.ChatChannelForUser> {
        return try {
            val result = collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_CHAT_CHANNEL)
                .document(studentID).get().await().toObject<Teachers.ChatChannelForUser>()
            FirebaseResponse.Success(result)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }

    suspend fun addChatChannelForStudent(
        studentID: String,
        teacherID: String,
        chatChannelID: Teachers.ChatChannelForUser
    ): FirebaseResponse<Boolean> {
        return try {
            collectionRef.document(studentID)
                .collection(Teachers.SUB_COLLECTION_CHAT_CHANNEL)
                .document(teacherID).set(chatChannelID).await()
            FirebaseResponse.Success(true)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }

    suspend fun addOfAllCourse(course: Teachers.Courses): FirebaseResponse<Boolean> {
        return try {

            collectionRefAllCourses.document(course.courseID).set(course).await()

            FirebaseResponse.Success(true)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }

    suspend fun addOfChatChannel(
        chatChannelID: String,
        chatChannel: Teachers.ChatChannel
    ): FirebaseResponse<Boolean> {
        return try {

            collectionRefChatChannel.document(chatChannelID).set(chatChannel).await()


            FirebaseResponse.Success(true)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }

    suspend fun addOfMessageChannel(
        chatChannelID: String,
        messages: TextMessage
    ): FirebaseResponse<Boolean> {
        return try {

            collectionRefChatChannel.document(chatChannelID)
                .collection("Messages")
                .document()
                .set(messages).await()

            FirebaseResponse.Success(true)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }

    suspend fun getOfMessageChannel(
        chatChannelID: String,
    ): FirebaseResponse<MutableList<TextMessage>> {
        return try {

            val message = collectionRefChatChannel.document(chatChannelID)
                .collection("Messages")
                .orderBy("timeOfMessage").get().await().toObjects<TextMessage>() as MutableList

            FirebaseResponse.Success(message)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }
    //endregion

    //region Get all courses
    suspend fun getAllCourses(teacherID: String): FirebaseResponse<MutableList<Teachers.Courses>> {

        return try {
            val course =
                collectionRefTeachers.document(teacherID)
                    .collection(Teachers.SUB_COLLECTION_COURSES).get().await()
                    .toObjects<Teachers.Courses>() as MutableList
            FirebaseResponse.Success(course)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }

    }

    suspend fun getAllStudentOfCourse(
        teacherID: String,
        courseID: String
    ): FirebaseResponse<MutableList<Teachers.CourseStudents>> {


        return try {
            val students = collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_COURSE_STUDENTS)
                .get().await().toObjects<Teachers.CourseStudents>() as MutableList
            FirebaseResponse.Success(students)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }

    }

    //endregion
    /*----------------------------------*//*----------------------------------*/
    //region Add lecture
    suspend fun addLecture(
        lecture: Teachers.LecturesOfCourse,
        teacherID: String
    ): FirebaseResponse<Boolean> {
        return try {
            collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(lecture.courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .document(lecture.lectureID)
                .set(lecture).await()

            /* val mLecture = Students.LecturesOfCourse(
                 lecture.lectureID, lecture.courseID,
                 lecture.teacherID,
                 lecture.lectureName,
                 lecture.lectureDescription,
                 lecture.lectureVideo,
                 lecture.lectureVideoName,
                 lastLecture = lecture.lastLecture
             )
             addLectureInStudentsSide(mLecture, teacherID)*/

            FirebaseResponse.Success(true)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }

    private suspend fun addLectureInStudentsSide(
        lecture: Students.LecturesOfCourse,
        teacherID: String
    ): FirebaseResponse<Boolean> {
        return try {
            val studentsOfCourse = collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(lecture.courseID)
                .collection(Teachers.SUB_COLLECTION_COURSE_STUDENTS)
                .get().await().toObjects<Teachers.CourseStudents>() as MutableList

            for (i in studentsOfCourse) {
                collectionRef.document(i.StudentID)
                    .collection(Teachers.SUB_COLLECTION_COURSES)
                    .document(lecture.courseID)
                    .collection(Teachers.SUB_COLLECTION_LECTURES)
                    .document(lecture.lectureID)
                    .set(lecture).await()
            }

            FirebaseResponse.Success(true)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }


    suspend fun addLectureInStudentsSide2(
        lecture: Students.LecturesOfCourse,
        studentID: String
    ): FirebaseResponse<Boolean> {
        return try {
            collectionRef.document(studentID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(lecture.courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .document(lecture.lectureID)
                .set(lecture).await()

            FirebaseResponse.Success(true)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }
    //endregion

    //region Get all lectures
    suspend fun getAllLecturesOfCourse(
        teacherID: String,
        courseID: String
    ): FirebaseResponse<MutableList<Teachers.LecturesOfCourse>> {
        return try {
            val lecture = collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .get().await().toObjects<Teachers.LecturesOfCourse>() as MutableList

            FirebaseResponse.Success(lecture)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }

    suspend fun getLectureOfCourse(
        studentID: String,
        courseID: String,
        lectureID: String
    ): FirebaseResponse<Students.LecturesOfCourse> {
        return try {
            val lecture = collectionRef.document(studentID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .document(lectureID)
                .get().await().toObject<Students.LecturesOfCourse>()

            FirebaseResponse.Success(lecture)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }
    //endregion
    /*----------------------------------*//*----------------------------------*/

    //region Add assignment
    suspend fun addAssignment(
        assignment: Teachers.AssignmentsOfLecture,
        teacherID: String,
        courseID: String,
        lectureID: String,
    ): FirebaseResponse<Boolean> {
        return try {
            collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .document(lectureID)
                .collection(Teachers.SUB_COLLECTION_ASSIGNMENTS)
                .document(assignment.assignmentID)
                .set(assignment).await()

            FirebaseResponse.Success(true)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }

    //endregion

    //region Get all assignments of Lecture
    suspend fun getAllAssignmentsOfLecture(
        teacherID: String,
        courseID: String,
        lectureID: String,
    ): FirebaseResponse<MutableList<Teachers.AssignmentsOfLecture>> {
        return try {
            val assignment = collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .document(lectureID).collection(Teachers.SUB_COLLECTION_ASSIGNMENTS)
                .get().await().toObjects<Teachers.AssignmentsOfLecture>() as MutableList

            FirebaseResponse.Success(assignment)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }

    suspend fun getAssignmentOfLecture(
        teacherID: String,
        courseID: String,
        lectureID: String,
        assignmentID: String
    ): FirebaseResponse<Teachers.AssignmentsOfLecture> {
        return try {
            val assignment = collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .document(lectureID).collection(Teachers.SUB_COLLECTION_ASSIGNMENTS)
                .document(assignmentID)
                .get().await().toObject<Teachers.AssignmentsOfLecture>()

            FirebaseResponse.Success(assignment)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }


    suspend fun getAllSubmissionOfAssignment(
        teacherID: String,
        courseID: String,
        lectureID: String,
        assignmentID: String
    ): FirebaseResponse<MutableList<Students.SubmissionAssignmentsOfLecture>> {
        return try {
            val assignment = collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .document(lectureID).collection(Teachers.SUB_COLLECTION_ASSIGNMENTS)
                .document(assignmentID)
                .collection(Teachers.SUB_COLLECTION_Submission_ASSIGNMENTS)
                .get().await().toObjects<Students.SubmissionAssignmentsOfLecture>() as MutableList

            FirebaseResponse.Success(assignment)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }
    //endregion

    /*----------------------------------*//*----------------------------------*/

    //region Edit course
    suspend fun editCourse(
        newCourseMap: Map<String, Any>,
        teacherID: String,
        courseID: String,
    ): FirebaseResponse<Boolean> {

        return try {
            val course = collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES).whereEqualTo("courseID", courseID)
                .get().await()

            for (doc in course.documents) {
                collectionRefTeachers.document(teacherID)
                    .collection(Teachers.SUB_COLLECTION_COURSES)
                    .document(doc.id).set(newCourseMap, SetOptions.merge())
            }
            val allCourse = collectionRefAllCourses.whereEqualTo("courseID", courseID).get().await()

            for (doc in allCourse.documents) {
                collectionRefAllCourses.document(doc.id).set(newCourseMap, SetOptions.merge())
            }

            editCourseFromStudents(newCourseMap, teacherID, courseID)

            FirebaseResponse.Success(true)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message)
        }
    }

    private suspend fun editCourseFromStudents(
        newCourseMap: Map<String, Any>,
        teacherID: String,
        courseID: String,
    ): FirebaseResponse<Boolean> {

        return try {
            val studentsOfCourse = collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_COURSE_STUDENTS)
                .get().await().toObjects<Teachers.CourseStudents>() as MutableList

            for (i in studentsOfCourse) {

                val courseS = collectionRef.document(i.StudentID)
                    .collection(Students.SUB_COLLECTION_COURSES).whereEqualTo("courseID", courseID)
                    .get().await()

                for (doc in courseS.documents) {
                    collectionRef.document(i.StudentID)
                        .collection(Students.SUB_COLLECTION_COURSES)
                        .document(doc.id).set(newCourseMap, SetOptions.merge())
                }
            }

            FirebaseResponse.Success(true)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message)
        }
    }

    //endregion

    //region Delete Course
    suspend fun deleteCourse(
        teacherID: String,
        courseID: String,
        courseNameImage: String?,
    ): FirebaseResponse<Boolean> {

        return try {

            deleteCourseFromStudents(teacherID, courseID)

            collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID).delete().await()

            collectionRefAllCourses.document(courseID).delete().await()


            storageRef.child("$CHILD_PATH_COURSES/${courseNameImage}").delete().await()

            FirebaseResponse.Success(true)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message)
        }
    }

    private suspend fun deleteCourseFromStudents(
        teacherID: String,
        courseID: String,
    ): FirebaseResponse<Boolean> {

        return try {

            val studentsOfCourse = collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_COURSE_STUDENTS)
                .get().await().toObjects<Teachers.CourseStudents>() as MutableList

            for (i in studentsOfCourse) {
                collectionRef.document(i.StudentID)
                    .collection(Students.SUB_COLLECTION_COURSES)
                    .document(courseID).delete().await()

                val c = collectionRef.document(i.StudentID)
                    .collection(Teachers.SUB_COLLECTION_COURSES)
                    .document(courseID)
                    .get().await().toObject<Students.Courses>()


                if (c!!.finished == false) {
                    val user =
                        collectionRef.document(i.StudentID).get().await()
                            .toObject<Students.Student>()
                    val oldNumber = user!!.numberOfRegisteredCourses
                    val newNumber = oldNumber - 1
                    updateNumberOfRegisteredCourses(i.StudentID, newNumber)

                }

            }

            FirebaseResponse.Success(true)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message)
        }
    }

    private suspend fun updateNumberOfRegisteredCourses(
        studentID: String,
        numberOfRegisteredCourses: Int
    ): FirebaseResponse<Boolean> {

        return try {
            Firebase.firestore.runTransaction { transaction ->
                val userRef = collectionRef.document(studentID)
                transaction.update(userRef, "numberOfRegisteredCourses", numberOfRegisteredCourses)

                FirebaseResponse.Success(true)
            }.await()
        } catch (e: Exception) {
            FirebaseResponse.Failure(message = e.message)
        }
    }

    //endregion
    /*----------------------------------*//*----------------------------------*/

    //region Edit lecture
    suspend fun editLecture(
        newLectureMap: Map<String, Any>,
        teacherID: String,
        courseID: String,
        lectureID: String,
    ): FirebaseResponse<Boolean> {

        return try {
            val lecture = collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .whereEqualTo("lectureID", lectureID)
                .get().await()

            for (doc in lecture.documents) {
                collectionRefTeachers.document(teacherID)
                    .collection(Teachers.SUB_COLLECTION_COURSES)
                    .document(courseID)
                    .collection(Teachers.SUB_COLLECTION_LECTURES)
                    .document(doc.id).set(newLectureMap, SetOptions.merge())
            }

            editLectureFromStudent(newLectureMap, teacherID, courseID, lectureID)
            FirebaseResponse.Success(true)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message)
        }
    }


    private suspend fun editLectureFromStudent(
        newLectureMap: Map<String, Any>,
        teacherID: String,
        courseID: String,
        lectureID: String,
    ): FirebaseResponse<Boolean> {

        return try {

            val studentsOfCourse = collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_COURSE_STUDENTS)
                .get().await().toObjects<Teachers.CourseStudents>() as MutableList

            for (i in studentsOfCourse) {

                val courseS = collectionRef.document(i.StudentID)
                    .collection(Students.SUB_COLLECTION_COURSES)
                    .document(courseID)
                    .collection(Teachers.SUB_COLLECTION_LECTURES)
                    .whereEqualTo("lectureID", lectureID)
                    .get().await()

                for (doc in courseS.documents) {
                    collectionRef.document(i.StudentID)
                        .collection(Students.SUB_COLLECTION_COURSES)
                        .document(courseID)
                        .collection(Teachers.SUB_COLLECTION_LECTURES)
                        .document(doc.id).set(newLectureMap, SetOptions.merge())
                }
            }

            FirebaseResponse.Success(true)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message)
        }
    }
    //endregion

    //region Delete Lecture
    suspend fun deleteLecture(
        teacherID: String,
        courseID: String,
        lectureID: String,
        lectureVideoName: String?
    ): FirebaseResponse<Boolean> {

        return try {
            collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .document(lectureID).delete().await()

            storageRef.child("$CHILD_PATH_Lectures/${lectureVideoName}").delete().await()

            deleteLectureFromStudents(teacherID, courseID, lectureID)
            FirebaseResponse.Success(true)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message)
        }
    }


    private suspend fun deleteLectureFromStudents(
        teacherID: String,
        courseID: String,
        lectureID: String,
    ): FirebaseResponse<Boolean> {

        return try {
            val studentsOfCourse = collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_COURSE_STUDENTS)
                .get().await().toObjects<Teachers.CourseStudents>() as MutableList

            for (i in studentsOfCourse) {
                collectionRef.document(i.StudentID)
                    .collection(Students.SUB_COLLECTION_COURSES)
                    .document(courseID)
                    .collection(Teachers.SUB_COLLECTION_LECTURES)
                    .document(lectureID).delete().await()

            }



            FirebaseResponse.Success(true)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message)
        }
    }
    //endregion

    /*----------------------------------*//*----------------------------------*/
    //region Edit assignment
    suspend fun editAssignment(
        newAssignmentMap: Map<String, Any>,
        teacherID: String,
        courseID: String,
        lectureID: String,
        assignmentID: String
    ): FirebaseResponse<Boolean> {

        return try {
            val lecture = collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .document(lectureID)
                .collection(Teachers.SUB_COLLECTION_ASSIGNMENTS)
                .whereEqualTo("assignmentID", assignmentID)
                .get().await()

            for (doc in lecture.documents) {
                collectionRefTeachers.document(teacherID)
                    .collection(Teachers.SUB_COLLECTION_COURSES)
                    .document(courseID)
                    .collection(Teachers.SUB_COLLECTION_LECTURES)
                    .document(lectureID)
                    .collection(Teachers.SUB_COLLECTION_ASSIGNMENTS)
                    .document(doc.id).set(newAssignmentMap, SetOptions.merge())
            }

            FirebaseResponse.Success(true)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message)
        }
    }
    //endregion

    //region Delete assignment
    suspend fun deleteAssignment(
        teacherID: String,
        courseID: String,
        lectureID: String,
        assignmentID: String,
        assignmentFileName: String?,
    ): FirebaseResponse<Boolean> {

        return try {
            collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .document(lectureID)
                .collection(Teachers.SUB_COLLECTION_ASSIGNMENTS)
                .document(assignmentID)
                .delete().await()
            storageRef.child("$CHILD_PATH_ASSIGNMENT_OF_Lectures/${assignmentFileName}").delete()
                .await()

            FirebaseResponse.Success(true)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message)
        }
    }
    //endregion

    /*----------------------------------*//*----------------------------------*/
    //region Add chapter
    suspend fun addChapter(
        chapter: Teachers.ChapterOfLecture,
        teacherID: String,
        courseID: String,
        lectureID: String,
    ): FirebaseResponse<Boolean> {
        return try {
            collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .document(lectureID)
                .collection(Teachers.SUB_COLLECTION_CHAPTERS)
                .document(chapter.chapterID)
                .set(chapter).await()

            FirebaseResponse.Success(true)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }

    //endregion

    //region Get all chapters of Lecture
    suspend fun getAllChapterOfLecture(
        teacherID: String,
        courseID: String,
        lectureID: String,
    ): FirebaseResponse<MutableList<Teachers.ChapterOfLecture>> {
        return try {
            val chapter = collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .document(lectureID).collection(Teachers.SUB_COLLECTION_CHAPTERS)
                .get().await().toObjects<Teachers.ChapterOfLecture>() as MutableList

            FirebaseResponse.Success(chapter)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }

    suspend fun getChapterOfLecture(
        teacherID: String,
        courseID: String,
        lectureID: String,
        chapterID: String
    ): FirebaseResponse<Teachers.ChapterOfLecture> {
        return try {
            val chapter = collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .document(lectureID)
                .collection(Teachers.SUB_COLLECTION_CHAPTERS)
                .document(chapterID)
                .get().await().toObject<Teachers.ChapterOfLecture>()

            FirebaseResponse.Success(chapter)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }

    //endregion

    //region Edit assignment
    suspend fun editChapter(
        newChapterMap: Map<String, Any>,
        teacherID: String,
        courseID: String,
        lectureID: String,
        chapterID: String
    ): FirebaseResponse<Boolean> {

        return try {
            val lecture = collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .document(lectureID)
                .collection(Teachers.SUB_COLLECTION_CHAPTERS)
                .whereEqualTo("chapterID", chapterID)
                .get().await()

            for (doc in lecture.documents) {
                collectionRefTeachers.document(teacherID)
                    .collection(Teachers.SUB_COLLECTION_COURSES)
                    .document(courseID)
                    .collection(Teachers.SUB_COLLECTION_LECTURES)
                    .document(lectureID)
                    .collection(Teachers.SUB_COLLECTION_CHAPTERS)
                    .document(doc.id).set(newChapterMap, SetOptions.merge())
            }

            FirebaseResponse.Success(true)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message)
        }
    }
    //endregion

    //region Delete assignment
    suspend fun deleteChapter(
        teacherID: String,
        courseID: String,
        lectureID: String,
        chapterID: String,
        chapterFileName: String?,
    ): FirebaseResponse<Boolean> {

        return try {
            collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .document(lectureID)
                .collection(Teachers.SUB_COLLECTION_CHAPTERS)
                .document(chapterID)
                .delete().await()
            storageRef.child("$CHILD_PATH_CHAPTERS_OF_Lectures/${chapterFileName}").delete().await()

            FirebaseResponse.Success(true)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message)
        }
    }
    //endregion


    suspend fun checkLoggedInState(teacherID: String): FirebaseResponse<Teachers.Teacher> {
        return try {
            val user =
                collectionRefTeachers.document(teacherID).get().await().toObject<Teachers.Teacher>()
            FirebaseResponse.Success(user)
        } catch (e: Exception) {
            FirebaseResponse.Failure(message = e.message)
        }
    }

    suspend fun updateTeacherLoggedInState(teacherID: String): FirebaseResponse<Boolean> {

        return try {
            Firebase.firestore.runTransaction { transaction ->
                val userRef = collectionRefTeachers.document(teacherID)
                val teacher = transaction.get(userRef)
                transaction.update(userRef, "firstLogin", true)

                // return null means that the transaction was successful.
                FirebaseResponse.Success(true)
            }.await()
        } catch (e: Exception) {
            FirebaseResponse.Failure(message = e.message)
        }
    }

    suspend fun updateCourseVisibilityState(
        course: Teachers.Courses,
        visibility: Boolean
    ): FirebaseResponse<Boolean> {

        return try {
            Firebase.firestore.runTransaction { transaction ->
                val userRef = collectionRefTeachers.document(course.teacherID)
                    .collection(Teachers.SUB_COLLECTION_COURSES)
                    .document(course.courseID)

                transaction.update(userRef, "visibility", visibility)

                val allCourses = collectionRefAllCourses.document(course.courseID)
                transaction.update(allCourses, "visibility", visibility)

                FirebaseResponse.Success(true)
            }.await()
        } catch (e: Exception) {
            FirebaseResponse.Failure(message = e.message)
        }
    }

    suspend fun updateCoursePublishingState(
        course: Teachers.Courses,
    ): FirebaseResponse<Boolean> {

        return try {
            Firebase.firestore.runTransaction { transaction ->
                val userRef = collectionRefTeachers.document(course.teacherID)
                    .collection(Teachers.SUB_COLLECTION_COURSES)
                    .document(course.courseID)

                transaction.update(userRef, "publishing", true)

                val allCourses = collectionRefAllCourses.document(course.courseID)
                transaction.update(allCourses, "publishing", true)

                FirebaseResponse.Success(true)
            }.await()
        } catch (e: Exception) {
            FirebaseResponse.Failure(message = e.message)
        }
    }

    suspend fun updateCourseVisibilityStateFromStudents(
        studentID: String,
        course: Teachers.Courses,
        visibility: Boolean
    ): FirebaseResponse<Boolean> {

        return try {
            Firebase.firestore.runTransaction { transaction ->
                val userRef = collectionRef.document(studentID)
                    .collection(Students.SUB_COLLECTION_COURSES)
                    .document(course.courseID)
                transaction.update(userRef, "visibility", visibility)
                // return null means that the transaction was successful.
                FirebaseResponse.Success(true)
            }.await()
        } catch (e: Exception) {
            FirebaseResponse.Failure(message = e.message)
        }
    }

    suspend fun updateLectureVisibilityState(
        lecture: Teachers.LecturesOfCourse,
        visibility: Boolean
    ): FirebaseResponse<Boolean> {

        return try {
            Firebase.firestore.runTransaction { transaction ->
                val userRef = collectionRefTeachers.document(lecture.teacherID)
                    .collection(Teachers.SUB_COLLECTION_COURSES)
                    .document(lecture.courseID)
                    .collection(Teachers.SUB_COLLECTION_LECTURES)
                    .document(lecture.lectureID)

                transaction.update(userRef, "visibility", visibility)

                FirebaseResponse.Success(true)
            }.await()
        } catch (e: Exception) {
            FirebaseResponse.Failure(message = e.message)
        }
    }

    suspend fun updateLectureVisibilityStateFromStudents(
        studentID: String,
        lecture: Teachers.LecturesOfCourse,
        visibility: Boolean
    ): FirebaseResponse<Boolean> {

        return try {
            Firebase.firestore.runTransaction { transaction ->

                val userRef = collectionRef.document(studentID)
                    .collection(Students.SUB_COLLECTION_COURSES)
                    .document(lecture.courseID)
                    .collection(Teachers.SUB_COLLECTION_LECTURES)
                    .document(lecture.lectureID)
                transaction.update(userRef, "visibility", visibility)

                FirebaseResponse.Success(true)
            }.await()
        } catch (e: Exception) {
            FirebaseResponse.Failure(message = e.message)
        }
    }

    suspend fun updateAssignmentVisibilityState(
        assignment: Teachers.AssignmentsOfLecture,
        visibility: Boolean
    ): FirebaseResponse<Boolean> {

        return try {
            Firebase.firestore.runTransaction { transaction ->
                val userRef = collectionRefTeachers.document(assignment.teacherID)
                    .collection(Teachers.SUB_COLLECTION_COURSES)
                    .document(assignment.courseID)
                    .collection(Teachers.SUB_COLLECTION_LECTURES)
                    .document(assignment.lectureID)
                    .collection(Teachers.SUB_COLLECTION_ASSIGNMENTS)
                    .document(assignment.assignmentID)

                transaction.update(userRef, "visibility", visibility)

                FirebaseResponse.Success(true)
            }.await()
        } catch (e: Exception) {
            FirebaseResponse.Failure(message = e.message)
        }
    }

    suspend fun updateChapterVisibilityState(
        chapter: Teachers.ChapterOfLecture,
        visibility: Boolean
    ): FirebaseResponse<Boolean> {

        return try {
            Firebase.firestore.runTransaction { transaction ->
                val userRef = collectionRefTeachers.document(chapter.teacherID)
                    .collection(Teachers.SUB_COLLECTION_COURSES)
                    .document(chapter.courseID)
                    .collection(Teachers.SUB_COLLECTION_LECTURES)
                    .document(chapter.lectureID)
                    .collection(Teachers.SUB_COLLECTION_CHAPTERS)
                    .document(chapter.chapterID)

                transaction.update(userRef, "visibility", visibility)

                FirebaseResponse.Success(true)
            }.await()
        } catch (e: Exception) {
            FirebaseResponse.Failure(message = e.message)
        }
    }

    suspend fun getAllStudentsOfCourse(
        teacherID: String,
        courseID: String
    ): FirebaseResponse<MutableList<Teachers.CourseStudents>> {
        return try {
            val students = collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_COURSE_STUDENTS)
                .get().await().toObjects<Teachers.CourseStudents>() as MutableList

            FirebaseResponse.Success(students)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }
}


