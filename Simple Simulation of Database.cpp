#include <iostream>
#include <map>
#include <set>
#include <string>
#include <vector>
#include <sstream>
#include <algorithm>
#include <exception>
#include <iomanip>
#include <stdexcept>
using namespace std;

class Date {
public:
  Date() : year{ 0 }, month{ 0 }, day{ 0 } {}
  Date(int& y, int& m, int& d)
  {
    year = y;
    if (m < 1 || m > 12)
    {
      throw runtime_error("Month value is invalid: " + to_string(m));
    }
    month = m;
    if (d < 1 || d > 31)
    {
      throw runtime_error("Day value is invalid: " + to_string(d));
    }
    day = d;
  }
  int GetYear() const
  {
    return year;
  }
  int GetMonth() const
  {
    return month;
  }
  int GetDay() const
  {
    return day;
  }
private:
  int year;
  int month;
  int day;
};

bool operator < (const Date& lhs, const Date& rhs)
{
  vector<int> l = { lhs.GetYear(), lhs.GetMonth(), lhs.GetDay() };
  vector<int> r = { rhs.GetYear(), rhs.GetMonth(), rhs.GetDay() };
  return l < r;
}

ostream& operator << (ostream& stream, const Date& date)
{
  stream << setw(4) << setfill('0') <<
    date.GetYear() << '-' << setw(2) << setfill('0') << date.GetMonth() << '-' <<
    setw(2) << setfill('0') << date.GetDay();
  return stream;
}

class Database {
public:
  void AddEvent(const Date& date, const string& event)
  {
    data[date].insert(event);
  }
  bool DeleteEvent(const Date& date, const string& event)
  {
    if (data.count(date) != 0)
    {
      if (data.at(date).count(event) != 0)
      {
        data.at(date).erase(event);
        return true;
      }
    }
    return false;
  }

  int DeleteDate(const Date& date)
  {
    int N = 0;
    if (data.count(date) != 0)
    {
      N = data.at(date).size();
      data.erase(date);
    }
    return N;
  }

  void Find(const Date& date) const
  {
    if (data.count(date) != 0)
    {
      for (const auto& it : data.at(date))
      {
        cout << it << endl;
      }
    }
  }

  void Print() const
  {
    for (const auto& it : data)
    {
      for (const auto& it1 : it.second)
      {
        cout << it.first << ' ' << it1 << endl;
      }
    }
  }
private:
  map<Date, set<string>> data;
};

Date ReadDate(const string& dd)
{
  stringstream ss(dd);
  int y = 0, m = 0, d = 0;
  bool correct = true;
  correct = correct && (ss >> y);
  correct = correct && (ss.peek() == '-');
  ss.ignore(1);
  correct = correct && (ss >> m);
  correct = correct && (ss.peek() == '-');
  ss.ignore(1);
  correct = correct && (ss >> d);
  correct = correct && ss.eof();
  if (!correct)
  {
    throw runtime_error("Wrong date format: " + dd);
  }
  return { y, m, d };
}


int main() {
  Database db;
  string command = "";
  string cmd = "";
  while (getline(cin, command)) {
    if (command.empty())
    {
      continue;
    }
    try
    {
      stringstream ss(command);
      string dd = "";
      ss >> cmd;
      if (cmd == "Print")
      {
        db.Print();
        continue;
      }
      ss >> dd;
      if (cmd == "Add")
      {
        string act;
        ss >> act;
        const Date date = ReadDate(dd);
        db.AddEvent(date, act);
      }
      else if (cmd == "Del")
      {
        string act = "";
        ss >> act;
        const Date date = ReadDate(dd);
        if (act.size() != 0)
        {
          if (db.DeleteEvent(date, act))
          {
            cout << "Deleted successfully\n";
          }
          else
          {
            cout << "Event not found\n";
          }
        }
        else
        {
          cout << "Deleted " << db.DeleteDate(date) << " events\n";
        }
      }
      else if (cmd == "Find")
      {
        const Date date = ReadDate(dd);
        db.Find(date);
      }
      else
      {
        throw runtime_error("Unknown command: " + cmd);
      }
    }
    catch (const exception& ex)
    {
      cout << ex.what() << endl;
    }
    command = "";
    cmd = "";
  }
  return 0;
}
