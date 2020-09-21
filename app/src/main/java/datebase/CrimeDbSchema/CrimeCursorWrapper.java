package datebase.CrimeDbSchema;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import datebase.CrimeDbSchema.CrimeDbSchema.CrimeTable;
import wengxiaoyang.hziee.criminalintent.Crime;

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        Long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        int isRequirePolice = getInt(getColumnIndex(CrimeTable.Cols.REQUIREPOLICE));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));
        String phone = getString(getColumnIndex(CrimeTable.Cols.PHONE));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setRequirePolice(isRequirePolice != 0);
        crime.setSuspect(suspect);
        crime.setPhone(phone);

        return crime;
    }
}