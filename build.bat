@echo off
echo ============================================
echo   Building Event Management System...
echo ============================================

cd /d "%~dp0"

if not exist "out" mkdir out

echo Compiling Java sources...
javac -cp "lib\*" -d out -sourcepath src ^
  src\util\Validation.java ^
  src\util\SessionManager.java ^
  src\util\IDGenerator.java ^
  src\util\QRGenerator.java ^
  src\model\User.java ^
  src\model\Event.java ^
  src\model\Registration.java ^
  src\model\Payment.java ^
  src\model\Pass.java ^
  src\model\Feedback.java ^
  src\model\Attendance.java ^
  src\database\DBConnection.java ^
  src\dao\UserDAO.java ^
  src\dao\EventDAO.java ^
  src\dao\RegistrationDAO.java ^
  src\dao\PaymentDAO.java ^
  src\dao\PassDAO.java ^
  src\dao\FeedbackDAO.java ^
  src\dao\AttendanceDAO.java ^
  src\dao\WaitingListDAO.java ^
  src\ds\EventStore.java ^
  src\ds\EventBST.java ^
  src\ds\WaitingQueue.java ^
  src\ds\UndoStack.java ^
  src\service\AuthService.java ^
  src\service\EventService.java ^
  src\service\RegistrationService.java ^
  src\service\UserService.java ^
  src\service\PaymentService.java ^
  src\service\FeedbackService.java ^
  src\gui\ThemeConfig.java ^
  src\gui\MainFrame.java ^
  src\gui\SplashScreen.java ^
  src\gui\LoginPanel.java ^
  src\gui\RegisterPanel.java ^
  src\gui\AdminDashboard.java ^
  src\gui\StaffDashboard.java ^
  src\gui\UserDashboard.java ^
  src\gui\EventListPanel.java ^
  src\gui\EventDetailPanel.java ^
  src\gui\AddEditEventPanel.java ^
  src\gui\PaymentPanel.java ^
  src\gui\PassPanel.java ^
  src\gui\UserManagementPanel.java ^
  src\gui\ProfilePanel.java ^
  src\gui\FeedbackPanel.java ^
  src\gui\CertificatePanel.java ^
  src\main\Main.java

if %errorlevel% neq 0 (
    echo.
    echo BUILD FAILED!
    pause
    exit /b 1
)

echo.
echo ============================================
echo   Build successful!
echo   Run 'run.bat' to start the application.
echo ============================================
pause
