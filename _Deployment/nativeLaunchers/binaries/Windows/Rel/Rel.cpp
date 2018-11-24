// Rel native launcher for Windows.

#include "pch.h"
#include <windows.h>
#include <stdio.h>
#include <shellapi.h>
#include <direct.h>
#include <PathCch.h>
#include <string>
#include <fstream>
#include <streambuf>

LPWSTR ConvertToLPWSTR(const std::wstring& s)
{
	LPWSTR ws = new wchar_t[s.size() + 1]; // +1 for zero at the end
	copy(s.begin(), s.end(), ws);
	ws[s.size()] = 0; // zero at the end
	return ws;
}

int APIENTRY wWinMain(_In_ HINSTANCE hInstance,
	_In_opt_ HINSTANCE hPrevInstance,
	_In_ LPWSTR    lpCmdLine,
	_In_ int       nCmdShow)
{
	SECURITY_ATTRIBUTES sa;
	sa.nLength = sizeof(sa);
	sa.lpSecurityDescriptor = NULL;
	sa.bInheritHandle = TRUE;

	LPWSTR *szArgList;
	int argCount;

	szArgList = CommandLineToArgvW(GetCommandLine(), &argCount);
	if (szArgList == NULL)
	{
		MessageBox(NULL, L"Unable to parse command line", L"Error", MB_OK | MB_ICONERROR);
		return 10;
	}

	DWORD mode = FILE_SHARE_WRITE | FILE_SHARE_READ;
	HANDLE h = CreateFile(TEXT("Rel.log"), GENERIC_WRITE, mode, &sa, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);

	PROCESS_INFORMATION pi;
	STARTUPINFO si;
	BOOL ret = FALSE;
	DWORD flags = CREATE_NO_WINDOW;

	ZeroMemory(&pi, sizeof(PROCESS_INFORMATION));
	ZeroMemory(&si, sizeof(STARTUPINFO));
	si.cb = sizeof(STARTUPINFO);
	si.dwFlags |= STARTF_USESTDHANDLES;
	si.hStdInput = NULL;
	si.hStdError = h;
	si.hStdOutput = h;

	// Convert first argument of szArgList[] (full pathspec to this executable) to path where executable is found
	HRESULT result = PathCchRemoveFileSpec(szArgList[0], wcslen(szArgList[0]));
	_wchdir(szArgList[0]);

	// Read the ini file
	std::wstring iniFileName(L"lib\\Rel.ini");
	std::ifstream configfile(iniFileName);
	std::wstring cmd((std::istreambuf_iterator<char>(configfile)), std::istreambuf_iterator<char>());

	// Empty or no ini file?
	if (cmd.length() == 0) {
		MessageBox(NULL, (std::wstring(L"Unable to find ") + iniFileName).c_str(), L"Missing or Damaged .ini File", MB_OK | MB_ICONERROR);
		LocalFree(szArgList);
		return 10;
	}

	// Include command-line args.
	std::wstring args(L"");
	for (int i = 1; i < argCount; i++)
		args += std::wstring(L" ") + std::wstring(szArgList[i]);

	LocalFree(szArgList);

	// Launch
	if (CreateProcess(NULL, ConvertToLPWSTR(cmd + args), NULL, NULL, TRUE, flags, NULL, NULL, &si, &pi))
	{
		CloseHandle(pi.hProcess);
		CloseHandle(pi.hThread);

		return 0;
	}

	MessageBox(NULL, cmd.c_str(), L"Unable to Launch", MB_OK | MB_ICONERROR);

	return -1;
}
